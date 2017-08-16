package org.tio.http.server.handler;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.http.common.Cookie;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.RequestLine;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.listener.IHttpServerListener;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.util.ClassUtils;
import org.tio.http.server.util.Resps;
import org.tio.utils.cache.guava.GuavaCache;

import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.ClassUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class DefaultHttpRequestHandler implements IHttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

	//	/**
	//	 * 静态资源的CacheName
	//	 * key:   path 譬如"/index.html"
	//	 * value: HttpResponse
	//	 */
	//	private static final String STATIC_RES_CACHENAME = "TIO_HTTP_STATIC_RES";

	/**
	 * 静态资源的CacheName
	 * key:   path 譬如"/index.html"
	 * value: FileCache
	 */
	private static final String STATIC_RES_CONTENT_CACHENAME = "TIO_HTTP_STATIC_RES_CONTENT";

	protected HttpServerConfig httpConfig;

	protected Routes routes = null;

	private IHttpServerListener httpServerListener;

	//	private LoadingCache<String, HttpSession> loadingCache = null;

	/**
	 * 
	 * @param httpConfig
	 * @author: tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpServerConfig httpConfig) {
		this.httpConfig = httpConfig;

		if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
			//			GuavaCache.register(STATIC_RES_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
			GuavaCache.register(STATIC_RES_CONTENT_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
		}

		//		Integer concurrencyLevel = 8;
		//		Long expireAfterWrite = null;
		//		Long expireAfterAccess = httpConfig.getSessionTimeout();
		//		Integer initialCapacity = 10;
		//		Integer maximumSize = 100000000;
		//		boolean recordStats = false;
		//		loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, expireAfterWrite, expireAfterAccess, initialCapacity, maximumSize, recordStats);
	}

	private Cookie getSessionCookie(HttpRequest httpRequest, HttpServerConfig httpConfig) throws ExecutionException {
		Cookie sessionCookie = httpRequest.getCookie(httpConfig.getSessionCookieName());
		return sessionCookie;
	}

	//	private static String randomCookieValue() {
	//		return RandomUtil.randomUUID();
	//	}

	/**
	 * 
	 * @param httpConfig
	 * @param routes
	 * @author: tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpServerConfig httpConfig, Routes routes) {
		this(httpConfig);
		this.routes = routes;
	}

	/**
	 * 创建httpsession
	 * @return
	 * @author: tanyaowu
	 */
	private HttpSession createHttpSession() {
		String sessionId = httpConfig.getSessionIdGenerator().sessionId(httpConfig);
		HttpSession httpSession = new HttpSession(sessionId);
		return httpSession;
	}

	private void processCookieBeforeHandler(HttpRequest request, RequestLine requestLine, ChannelContext channelContext) throws ExecutionException {
		Cookie cookie = getSessionCookie(request, httpConfig);
		HttpSession httpSession = null;
		if (cookie == null) {
			httpSession = createHttpSession();
		} else {
			//			httpSession = (HttpSession)httpSession.getAtrribute(SESSIONID_KEY);//loadingCache.getIfPresent(sessionCookie.getValue());
			String sessionId = cookie.getValue();
			httpSession = (HttpSession) httpConfig.getSessionStore().get(sessionId);
			if (httpSession == null) {
				log.info("{} session【{}】超时", channelContext, sessionId);
				httpSession = createHttpSession();
			}
		}
		channelContext.setAttribute(httpSession);
	}

	private void processCookieAfterHandler(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext, HttpResponse httpResponse) throws ExecutionException {
		HttpSession httpSession = (HttpSession) channelContext.getAttribute();//.getHttpSession();//not null
		Cookie cookie = getSessionCookie(httpRequest, httpConfig);
		String sessionId = null;

		if (cookie == null) {
			String domain = httpRequest.getHeader(HttpConst.RequestHeaderKey.Host);
			String name = httpConfig.getSessionCookieName();
			long maxAge = httpConfig.getSessionTimeout();
			//			maxAge = Integer.MAX_VALUE; //把过期时间掌握在服务器端

			sessionId = httpSession.getSessionId();//randomCookieValue();

			cookie = new Cookie(domain, name, sessionId, maxAge);
			httpResponse.addCookie(cookie);
			httpConfig.getSessionStore().put(sessionId, httpSession);
			log.info("{} 创建会话Cookie, {}", channelContext, cookie);
		} else {
			sessionId = cookie.getValue();
			HttpSession httpSession1 = (HttpSession) httpConfig.getSessionStore().get(sessionId);

			if (httpSession1 == null) {//有cookie但是超时了
				sessionId = httpSession.getSessionId();
				String domain = httpRequest.getHeader(HttpConst.RequestHeaderKey.Host);
				String name = httpConfig.getSessionCookieName();
				long maxAge = httpConfig.getSessionTimeout();
				//				maxAge = Long.MAX_VALUE; //把过期时间掌握在服务器端

				cookie = new Cookie(domain, name, sessionId, maxAge);
				httpResponse.addCookie(cookie);

				httpConfig.getSessionStore().put(sessionId, httpSession);
			}
		}
	}

	@Override
	public HttpResponse handler(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) throws Exception {
		HttpResponse ret = null;
		try {
			processCookieBeforeHandler(httpRequest, requestLine, channelContext);
			HttpSession httpSession = (HttpSession) channelContext.getAttribute();

			//			GuavaCache guavaCache = GuavaCache.getCache(STATIC_RES_CACHENAME);
			//			ret = (HttpResponse) guavaCache.get(requestLine.getPath());
			//			if (ret != null) {
			//				log.info("从缓存中获取响应:{}", requestLine.getPath());
			//			}

			if (httpServerListener != null) {
				ret = httpServerListener.doBeforeHandler(httpRequest, requestLine, channelContext, ret);
				if (ret != null) {
					return ret;
				}
			}

			//			if (ret != null) {
			//				return ret;
			//			}

			String path = requestLine.getPath();
			String initPath = path;

			Method method = routes.pathMethodMap.get(initPath);
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
								paramValues[i] = httpConfig;
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
					throw new Exception(bean.getClass().getName() + "#" + method.getName() + "返回的对象不是" + HttpResponse.class.getName());
				}
			} else {
				GuavaCache contentCache = null;
				FileCache fileCache = null;
				if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
					contentCache = GuavaCache.getCache(STATIC_RES_CONTENT_CACHENAME);
					fileCache = (FileCache) contentCache.get(initPath);
				}
				if (fileCache != null) {
					byte[] bodyBytes = fileCache.getData();
					Map<String, String> headers = fileCache.getHeaders();
					long lastModified = fileCache.getLastModified();
					log.info("从缓存获取:[{}], {}", path, bodyBytes.length);

					ret = Resps.try304(httpRequest, lastModified, httpConfig);
					if (ret != null) {
						ret.addHeader(HttpConst.ResponseHeaderKey.tio_from_cache, "true");

						return ret;
					}

					ret = new HttpResponse(httpRequest, httpConfig);
					ret.setBody(bodyBytes, httpRequest);
					ret.addHeaders(headers);
					return ret;
				} else {
					String root = httpConfig.getRoot();
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
						ret = Resps.file(httpRequest, file, httpConfig);
						ret.setStaticRes(true);

						if (contentCache != null) {
							if (ret.getBody() != null && ret.getStatus() == HttpResponseStatus.C200) {

								String contentType = ret.getHeader(HttpConst.ResponseHeaderKey.Content_Type);
								String contentEncoding = ret.getHeader(HttpConst.ResponseHeaderKey.Content_Encoding);
								String lastModified = ret.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);

								Map<String, String> headers = new HashMap<>();
								if (StringUtils.isNotBlank(contentType)) {
									headers.put(HttpConst.ResponseHeaderKey.Content_Type, contentType);
								}
								if (StringUtils.isNotBlank(contentEncoding)) {
									headers.put(HttpConst.ResponseHeaderKey.Content_Encoding, contentEncoding);
								}
								if (StringUtils.isNotBlank(lastModified)) {
									headers.put(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
								}
								headers.put(HttpConst.ResponseHeaderKey.tio_from_cache, "true");

								fileCache = new FileCache(headers, file.lastModified(), ret.getBody());
								contentCache.put(initPath, fileCache);
								log.info("放入缓存:[{}], {}", initPath, ret.getBody().length);
							}
						}

						return ret;
					}
				}
			}

			ret = resp404(httpRequest, requestLine, channelContext);//Resps.html(httpRequest, "404--并没有找到你想要的内容", httpConfig.getCharset());
			return ret;
		} catch (Exception e) {
			logError(httpRequest, requestLine, e);
			ret = resp500(httpRequest, requestLine, channelContext, e);//Resps.html(httpRequest, "500--服务器出了点故障", httpConfig.getCharset());
			return ret;
		} finally {
			if (ret != null) {
				try {
					processCookieAfterHandler(httpRequest, requestLine, channelContext, ret);
					if (httpServerListener != null) {
						httpServerListener.doAfterHandler(httpRequest, requestLine, channelContext, ret);
					}
				} catch (Exception e) {
					logError(httpRequest, requestLine, e);
				}

				//				try {
				//					if (ret.isStaticRes() && (ret.getCookies() == null || ret.getCookies().size() == 0)) {
				//						ByteBuffer byteBuffer = HttpResponseEncoder.encode(ret, channelContext.getGroupContext(), channelContext, true);
				//						byte[] encodedBytes = byteBuffer.array();
				//						ret.setEncodedBytes(encodedBytes);

				//						GuavaCache guavaCache = GuavaCache.getCache(STATIC_RES_CACHENAME);
				//						guavaCache.put(requestLine.getPath(), ret);
				//					}
				//				} catch (Exception e) {
				//					logError(httpRequest, requestLine, e);
				//				}
			}
		}
	}

	private void logError(HttpRequest httpRequest, RequestLine requestLine, Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n").append("remote  :").append(httpRequest.getRemote());
		sb.append("\r\n").append("request :").append(requestLine.getLine());
		log.error(sb.toString(), e);

	}

	@Override
	public HttpResponse resp404(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) {
		String file404 = "/404.html";
		String root = httpConfig.getRoot();
		File file = new File(root, file404);
		if (file.exists()) {
			HttpResponse ret = Resps.redirect(httpRequest, file404 + "?initpath=" + requestLine.getPathAndQuery(), httpConfig);
			return ret;
		} else {
			HttpResponse ret = Resps.html(httpRequest, "404", httpConfig);
			return ret;
		}
	}

	@Override
	public HttpResponse resp500(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext, Throwable throwable) {
		String file500 = "/500.html";
		String root = httpConfig.getRoot();
		File file = new File(root, file500);
		if (file.exists()) {
			HttpResponse ret = Resps.redirect(httpRequest, file500 + "?initpath=" + requestLine.getPathAndQuery(), httpConfig);
			return ret;
		} else {
			HttpResponse ret = Resps.html(httpRequest, "500", httpConfig);
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
	}

	/**
	 * @return the httpConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpConfig;
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpServerConfig(HttpServerConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public IHttpServerListener getHttpServerListener() {
		return httpServerListener;
	}

	public void setHttpServerListener(IHttpServerListener httpServerListener) {
		this.httpServerListener = httpServerListener;
	}

}
