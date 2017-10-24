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
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.HttpSession;
import org.tio.http.server.listener.HttpServerInterceptor;
import org.tio.http.server.listener.HttpSessionListener;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.session.SessionCookieDecorator;
import org.tio.http.server.util.ClassUtils;
import org.tio.http.server.util.Resps;
import org.tio.utils.cache.guava.GuavaCache;

import com.xiaoleilu.hutool.convert.Convert;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.util.BeanUtil;
import com.xiaoleilu.hutool.util.ClassUtil;
import com.xiaoleilu.hutool.util.StrUtil;

/**
 *
 * @author tanyaowu
 *
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {
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

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public static void main(String[] args) {
	}

	protected HttpConfig httpConfig;

	protected Routes routes = null;

	//	private LoadingCache<String, HttpSession> loadingCache = null;

	private HttpServerInterceptor httpServerInterceptor;

	private HttpSessionListener httpSessionListener;
	
	private SessionCookieDecorator sessionCookieDecorator;

	private GuavaCache staticResCache;

	/**
	 *
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;

		if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
			//			GuavaCache.register(STATIC_RES_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
			staticResCache = GuavaCache.register(STATIC_RES_CONTENT_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
		}

		//		Integer concurrencyLevel = 8;
		//		Long timeToLiveSeconds = null;
		//		Long timeToIdleSeconds = httpConfig.getSessionTimeout();
		//		Integer initialCapacity = 10;
		//		Integer maximumSize = 100000000;
		//		boolean recordStats = false;
		//		loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, timeToLiveSeconds, timeToIdleSeconds, initialCapacity, maximumSize, recordStats);
	}

	//	private static String randomCookieValue() {
	//		return RandomUtil.randomUUID();
	//	}

	/**
	 *
	 * @param httpConfig
	 * @param routes
	 * @author tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Routes routes) {
		this(httpConfig);
		this.routes = routes;
	}

	/**
	 * 创建httpsession
	 * @return
	 * @author tanyaowu
	 */
	private HttpSession createSession(HttpRequest request) {
		String sessionId = httpConfig.getSessionIdGenerator().sessionId(httpConfig, request);
		HttpSession httpSession = new HttpSession(sessionId);
		if (httpSessionListener != null) {
			httpSessionListener.doAfterCreated(httpSession, httpConfig);
		}
		return httpSession;
	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public HttpServerInterceptor getHttpServerInterceptor() {
		return httpServerInterceptor;
	}

	private Cookie getSessionCookie(HttpRequest request, HttpConfig httpConfig) throws ExecutionException {
		Cookie sessionCookie = request.getCookie(httpConfig.getSessionCookieName());
		return sessionCookie;
	}

	/**
	 * @return the staticResCache
	 */
	public GuavaCache getStaticResCache() {
		return staticResCache;
	}

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		HttpResponse ret = null;
		RequestLine requestLine = request.getRequestLine();
		try {

			processCookieBeforeHandler(request, requestLine);
			HttpSession httpSession = request.getHttpSession();//(HttpSession) channelContext.getAttribute();

			//			GuavaCache guavaCache = GuavaCache.getCache(STATIC_RES_CACHENAME);
			//			ret = (HttpResponse) guavaCache.get(requestLine.getPath());
			//			if (ret != null) {
			//				log.info("从缓存中获取响应:{}", requestLine.getPath());
			//			}

			if (httpServerInterceptor != null) {
				ret = httpServerInterceptor.doBeforeHandler(request, requestLine, ret);
				if (ret != null) {
					return ret;
				}
			}
			requestLine = request.getRequestLine();
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
				Map<String, Object[]> params = request.getParams();
				if (parameterTypes == null || parameterTypes.length == 0) {
					obj = method.invoke(bean);
				} else {
					//赋值这段代码待重构，先用上
					Object[] paramValues = new Object[parameterTypes.length];
					int i = 0;
					for (Class<?> paramType : parameterTypes) {
						try {
							if (paramType.isAssignableFrom(HttpRequest.class)) {
								paramValues[i] = request;
							} else if (paramType == HttpSession.class) {
								paramValues[i] = httpSession;
							} else if (paramType.isAssignableFrom(HttpConfig.class)) {
								paramValues[i] = httpConfig;
							} else if (paramType.isAssignableFrom(ChannelContext.class)) {
								paramValues[i] = request.getChannelContext();
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
					if (obj == null) {
						ret = Resps.txt(request, "");//.json(request, obj + "");
					} else {
						ret = Resps.json(request, obj);
					}
					return ret;
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

					ret = Resps.try304(request, lastModified);
					if (ret != null) {
						ret.addHeader(HttpConst.ResponseHeaderKey.tio_from_cache, "true");

						return ret;
					}

					ret = new HttpResponse(request, httpConfig);
					ret.setBody(bodyBytes, request);
					ret.addHeaders(headers);
					return ret;
				} else {
					String pageRoot = httpConfig.getPageRoot();
					if (pageRoot != null) {
						String root = FileUtil.getAbsolutePath(pageRoot);
						File file = new File(root + path);
						if (!file.exists() || file.isDirectory()) {
							if (StringUtils.endsWith(path, "/")) {
								path = path + "index.html";
							} else {
								path = path + "/index.html";
							}
							file = new File(root, path);
						}

						if (file.exists()) {
							ret = Resps.file(request, file);
							ret.setStaticRes(true);

							if (contentCache != null && request.getIsSupportGzip()) {
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
			}

			ret = resp404(request, requestLine);//Resps.html(request, "404--并没有找到你想要的内容", httpConfig.getCharset());
			return ret;
		} catch (Exception e) {
			logError(request, requestLine, e);
			ret = resp500(request, requestLine, e);//Resps.html(request, "500--服务器出了点故障", httpConfig.getCharset());
			return ret;
		} finally {
			if (ret != null) {
				try {
					processCookieAfterHandler(request, requestLine, ret);
					if (httpServerInterceptor != null) {
						httpServerInterceptor.doAfterHandler(request, requestLine, ret);
					}
				} catch (Exception e) {
					logError(request, requestLine, e);
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
				//					logError(request, requestLine, e);
				//				}
			}
		}
	}

	private void logError(HttpRequest request, RequestLine requestLine, Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n").append("remote  :").append(request.getRemote());
		sb.append("\r\n").append("request :").append(requestLine.getLine());
		log.error(sb.toString(), e);

	}

	private void processCookieAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse httpResponse) throws ExecutionException {
		HttpSession httpSession = request.getHttpSession();//(HttpSession) channelContext.getAttribute();//.getHttpSession();//not null
		Cookie cookie = getSessionCookie(request, httpConfig);
		String sessionId = null;

		if (cookie == null) {
			createSessionCookie(request, httpSession, httpResponse);
			log.info("{} 创建会话Cookie, {}", request.getChannelContext(), cookie);
		} else {
			sessionId = cookie.getValue();
			HttpSession httpSession1 = (HttpSession) httpConfig.getSessionStore().get(sessionId);

			if (httpSession1 == null) {//有cookie但是超时了
				createSessionCookie(request, httpSession, httpResponse);
			}
		}
	}
	
	private Cookie createSessionCookie(HttpRequest request, HttpSession httpSession, HttpResponse httpResponse) {
		String sessionId = httpSession.getId();
		String host = request.getHost();
		String domain = domainFromHost(host);
		
		String name = httpConfig.getSessionCookieName();
		long maxAge = httpConfig.getSessionTimeout();
		//				maxAge = Long.MAX_VALUE; //把过期时间掌握在服务器端

		Cookie sessionCookie = new Cookie(domain, name, sessionId, maxAge);
		
		if (sessionCookieDecorator != null) {
			sessionCookieDecorator.decorate(sessionCookie);
		}
		httpResponse.addCookie(sessionCookie);

		httpConfig.getSessionStore().put(sessionId, httpSession);
		
		return sessionCookie;
	}
	
	private static String domainFromHost(String host) {
		return StrUtil.subBefore(host, ":", false);
	}

	private void processCookieBeforeHandler(HttpRequest request, RequestLine requestLine) throws ExecutionException {
		Cookie cookie = getSessionCookie(request, httpConfig);
		HttpSession httpSession = null;
		if (cookie == null) {
			httpSession = createSession(request);
		} else {
			//			httpSession = (HttpSession)httpSession.getAttribute(SESSIONID_KEY);//loadingCache.getIfPresent(sessionCookie.getValue());
			String sessionId = cookie.getValue();
			httpSession = (HttpSession) httpConfig.getSessionStore().get(sessionId);
			if (httpSession == null) {
				log.info("{} session【{}】超时", request.getChannelContext(), sessionId);
				httpSession = createSession(request);
			}
		}
		request.setHttpSession(httpSession);
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
		return Resps.resp404(request, requestLine, httpConfig);
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
		return Resps.resp500(request, requestLine, httpConfig, throwable);
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public void setHttpServerInterceptor(HttpServerInterceptor httpServerInterceptor) {
		this.httpServerInterceptor = httpServerInterceptor;
	}

	/**
	 * @param staticResCache the staticResCache to set
	 */
	public void setStaticResCache(GuavaCache staticResCache) {
		this.staticResCache = staticResCache;
	}

	@Override
	public void clearStaticResCache(HttpRequest request) {
		if (staticResCache != null) {
			staticResCache.clear();
		}
	}

	public HttpSessionListener getHttpSessionListener() {
		return httpSessionListener;
	}

	public void setHttpSessionListener(HttpSessionListener httpSessionListener) {
		this.httpSessionListener = httpSessionListener;
	}

	public SessionCookieDecorator getSessionCookieDecorator() {
		return sessionCookieDecorator;
	}

	public void setSessionCookieDecorator(SessionCookieDecorator sessionCookieDecorator) {
		this.sessionCookieDecorator = sessionCookieDecorator;
	}

}
