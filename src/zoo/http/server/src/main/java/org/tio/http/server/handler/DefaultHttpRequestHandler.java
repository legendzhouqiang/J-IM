package org.tio.http.server.handler;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.utils.GuavaUtils;
import org.tio.http.common.Cookie;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.listener.IHttpServerListener;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.util.ClassUtils;
import org.tio.http.server.util.Resps;

import com.google.common.cache.LoadingCache;
import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.ClassUtil;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class DefaultHttpRequestHandler implements IHttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

	protected HttpServerConfig httpServerConfig;

	protected Routes routes = null;

	private IHttpServerListener httpServerListener;

	private LoadingCache<String, HttpSession> loadingCache = null;

	/**
	 * 
	 * @param httpServerConfig
	 * @author: tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;

		Integer concurrencyLevel = 8;
		Long expireAfterWrite = null;
		Long expireAfterAccess = httpServerConfig.getSessionTimeout();
		Integer initialCapacity = 10;
		Integer maximumSize = 100000000;
		boolean recordStats = false;
		loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, expireAfterWrite, expireAfterAccess, initialCapacity, maximumSize, recordStats);
	}

	private Cookie getSessionCookie(HttpRequest httpRequest, HttpServerConfig httpServerConfig, ChannelContext channelContext) throws ExecutionException {
		Cookie sessionCookie = httpRequest.getCookie(httpServerConfig.getSessionCookieName());
		return sessionCookie;
	}

//	private static String randomCookieValue() {
//		return RandomUtil.randomUUID();
//	}

	/**
	 * 
	 * @param httpServerConfig
	 * @param routes
	 * @author: tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpServerConfig httpServerConfig, Routes routes) {
		this(httpServerConfig);
		this.routes = routes;
	}

	private void processCookieBeforeHandler(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) throws ExecutionException {
		Cookie sessionCookie = getSessionCookie(httpRequest, httpServerConfig, channelContext);
		HttpSession httpSession = null;
		if (sessionCookie == null) {
			String sessionId = RandomUtil.randomUUID();
			httpSession = new HttpSession(httpServerConfig.getHttpSessionFactory().create(sessionId, httpServerConfig.getSessionTimeout(), channelContext.getGroupContext()), sessionId);
		} else {
			httpSession = loadingCache.getIfPresent(sessionCookie.getValue());
			if (httpSession == null) {
				log.info("{} session【{}】超时", channelContext, sessionCookie.getValue());
				String sessionId = RandomUtil.randomUUID();
				httpSession = new HttpSession(httpServerConfig.getHttpSessionFactory().create(sessionId, httpServerConfig.getSessionTimeout(), channelContext.getGroupContext()), sessionId);
			}
		}
		channelContext.setAttribute(httpSession);
//		httpRequest.setHttpSession(httpSession);
	}

	private void processCookieAfterHandler(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext, HttpResponse httpResponse) throws ExecutionException {
		HttpSession httpSession = (HttpSession)channelContext.getAttribute();//.getHttpSession();//not null

		Cookie sessionCookie = getSessionCookie(httpRequest, httpServerConfig, channelContext);
		String sessionCookieValue = null;
		if (sessionCookie == null) {
			//			createSessionCookie(httpRequest, httpServerConfig, channelContext, httpResponse);

			String domain = httpRequest.getHeader(HttpConst.RequestHeaderKey.Host);
			String name = httpServerConfig.getSessionCookieName();

			sessionCookieValue = httpSession.getSessionId();//randomCookieValue();
			long maxAge = httpServerConfig.getSessionTimeout();
			sessionCookie = new Cookie(domain, name, sessionCookieValue, maxAge);
			httpResponse.addCookie(sessionCookie);
			loadingCache.put(sessionCookieValue, httpSession);
			log.info("{} 创建会话Cookie, {}", channelContext, sessionCookie);
		} else {
			sessionCookieValue = sessionCookie.getValue();
			HttpSession httpSession1 = loadingCache.getIfPresent(sessionCookieValue);
			if (httpSession1 == null) {
				loadingCache.put(sessionCookieValue, httpSession);
			}
		}
	}

	@Override
	public HttpResponse handler(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) throws Exception {
		HttpResponse ret = null;
		processCookieBeforeHandler(httpRequest, requestLine, channelContext);
		HttpSession httpSession = (HttpSession)channelContext.getAttribute();
		try {
			if (httpServerListener != null) {
				ret = httpServerListener.doBeforeHandler(httpRequest, requestLine, channelContext);
				if (ret != null) {
					return ret;
				}
			}

			String path = requestLine.getPath();

			Method method = routes.pathMethodMap.get(path);
			if (method != null) {
				String[] paramnames = routes.methodParamnameMap.get(method);
				Class<?>[] parameterTypes = method.getParameterTypes();

				Object bean = routes.methodBeanMap.get(method);
				Object obj = null;
				Map<String, Object[]> params = httpRequest.getParams();
				if (parameterTypes == null || parameterTypes.length == 0) {
					obj = method.invoke(bean);
				} else {
					//赋值这段代码待重构，先用上
					Object[] paramValues = new Object[parameterTypes.length];
					int i = 0;
					for (Class<?> paramType : parameterTypes) {
						try {
							if (paramType.isAssignableFrom(HttpRequest.class)) {
								paramValues[i] = httpRequest;
							} else if (paramType.isAssignableFrom(HttpServerConfig.class)) {
								paramValues[i] = httpServerConfig;
							} else if (paramType.isAssignableFrom(ChannelContext.class)) {
								paramValues[i] = channelContext;
							} else if (paramType == HttpSession.class) {
								paramValues[i] = httpSession;
							} else {
								if (params != null) {
									if (ClassUtils.isSimpleTypeOrArray(paramType)) {
										//										paramValues[i] = Ognl.getValue(paramnames[i], (Object) params, paramType);
										Object[] value = params.get(paramnames[i]);
										if (value != null && value.length > 0) {
											if (paramType.isArray()) {
												paramValues[i] = Convert.convert(paramType, value);
											} else {
												paramValues[i] = Convert.convert(paramType, value[0]);
											}
										}
									} else {
										paramValues[i] = paramType.newInstance();//BeanUtil.mapToBean(params, paramType, true);
										Set<Entry<String, Object[]>> set = params.entrySet();
										label2: for (Entry<String, Object[]> entry : set) {
											String fieldName = entry.getKey();
											Object[] fieldValue = entry.getValue();

											PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(paramType, fieldName, true);
											if (propertyDescriptor == null) {
												continue label2;
											} else {
												Method writeMethod = propertyDescriptor.getWriteMethod();
												if (writeMethod == null) {
													continue label2;
												}
												writeMethod = ClassUtil.setAccessible(writeMethod);
												Class<?>[] clazzes = writeMethod.getParameterTypes();
												if (clazzes == null || clazzes.length != 1) {
													log.info("方法的参数长度不为1，{}.{}", paramType.getName(), writeMethod.getName());
													continue label2;
												}
												Class<?> clazz = clazzes[0];

												if (ClassUtils.isSimpleTypeOrArray(clazz)) {
													if (fieldValue != null && fieldValue.length > 0) {
														if (clazz.isArray()) {
															writeMethod.invoke(paramValues[i], Convert.convert(clazz, fieldValue));
														} else {
															writeMethod.invoke(paramValues[i], Convert.convert(clazz, fieldValue[0]));
														}
													}
												}
											}
										}
									}
								}
							}
						} catch (Exception e) {
							log.error(e.toString(), e);
						} finally {
							i++;
						}
					}
					obj = method.invoke(bean, paramValues);
				}

				if (obj instanceof HttpResponse) {
					ret = (HttpResponse) obj;
					return ret;
				} else {
					//					log.error(bean.getClass().getName() + "#"+method.getName()+"返回的对象不是" + HttpResponsePacket.class.getName());
					throw new Exception(bean.getClass().getName() + "#" + method.getName() + "返回的对象不是" + HttpResponse.class.getName());
				}
			} else {
				String root = httpServerConfig.getRoot();
				File file = new File(root, path);
				if ((!file.exists()) || file.isDirectory()) {
					if (StringUtils.endsWith(path, "/")) {
						path = path + "index.html";
					} else {
						path = path + "/index.html";
					}
					file = new File(root, path);
				}

				if (file.exists()) {
					ret = Resps.file(httpRequest, file);
					return ret;
				}
			}

			ret = resp404(httpRequest, requestLine, channelContext);//Resps.html(httpRequest, "404--并没有找到你想要的内容", httpServerConfig.getCharset());
			return ret;
		} catch (Exception e) {
			String errorlog = "";//"error occured,\r\n";
			errorlog += requestLine.getLine();// + "\r\n";
			//			errorlog += e.toString();
			log.error(errorlog, e);
			ret = resp500(httpRequest, requestLine, channelContext, e);//Resps.html(httpRequest, "500--服务器出了点故障", httpServerConfig.getCharset());
			return ret;
		} finally {
			if (ret != null) {
				processCookieAfterHandler(httpRequest, requestLine, channelContext, ret);
				if (httpServerListener != null) {
					httpServerListener.doAfterHandler(httpRequest, requestLine, channelContext, ret);
				}
			}

		}
	}

	@Override
	public HttpResponse resp404(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) {
		String file404 = "/404.html";
		String root = httpServerConfig.getRoot();
		File file = new File(root, file404);
		if (file.exists()) {
			HttpResponse ret = Resps.redirect(httpRequest, file404 + "?initpath=" + requestLine.getPathAndQuery());
			return ret;
		} else {
			HttpResponse ret = Resps.html(httpRequest, "404", httpRequest.getCharset());
			return ret;
		}
	}

	@Override
	public HttpResponse resp500(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext, Throwable throwable) {
		String file500 = "/500.html";
		String root = httpServerConfig.getRoot();
		File file = new File(root, file500);
		if (file.exists()) {
			HttpResponse ret = Resps.redirect(httpRequest, file500 + "?initpath=" + requestLine.getPathAndQuery());
			return ret;
		} else {
			HttpResponse ret = Resps.html(httpRequest, "500", httpRequest.getCharset());
			return ret;
		}
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public static void main(String[] args) {

		//		System.out.println(ClassUtil.isBasicType(String.class));
		//		System.out.println(ClassUtil.isBasicType(Object.class));
		//		System.out.println(ClassUtil.isBasicType(Integer.class));
		//		System.out.println(ClassUtil.isBasicType(int.class));
		//
		//		Map<String, String[]> params = new HashMap<>();
		//		String[] names = new String[] { "111" };
		//		params.put("id", names);
		//
		//		User user = BeanUtil.mapToBean(params, User.class, true);
		//
		//		try {
		//			Object obj = Ognl.getValue("id", (Object) params, (Class<?>) Integer.class);
		//			System.out.println(obj);
		//
		//		} catch (OgnlException e) {
		//			log.error(e.toString(), e);
		//		}
	}

	public static class User {
		private int[] id;

		/**
		 * @return the id
		 */
		public int[] getId() {
			return id;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(int[] id) {
			this.id = id;
		}
	}

	/**
	 * @return the httpServerConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}

	/**
	 * @param httpServerConfig the httpServerConfig to set
	 */
	public void setHttpServerConfig(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}

	public IHttpServerListener getHttpServerListener() {
		return httpServerListener;
	}

	public void setHttpServerListener(IHttpServerListener httpServerListener) {
		this.httpServerListener = httpServerListener;
	}

}
