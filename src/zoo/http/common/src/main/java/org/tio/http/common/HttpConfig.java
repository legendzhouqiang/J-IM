package org.tio.http.common;

import org.tio.http.common.handler.IHttpRequestHandler;
import org.tio.http.common.session.id.ISessionIdGenerator;
import org.tio.utils.cache.ICache;

import com.xiaoleilu.hutool.io.FileUtil;

/**
 * @author tanyaowu
 * 2017年8月15日 下午1:21:14
 */
public class HttpConfig {

	//	private static Logger log = LoggerFactory.getLogger(HttpConfig.class);

	/**
	 * 存放HttpSession对象的cacheName
	 */
	public static final String SESSION_CACHE_NAME = "tio-h-s";

	/**
	 * 存放sessionId的cookie name
	 */
	public static final String SESSION_COOKIE_NAME = "TwIxO";

	/**
	 * session默认的超时时间，单位：秒
	 */
	public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60;

	/**
	 * 默认的静态资源缓存时间，单位：秒
	 */
	public static final int MAX_LIVETIME_OF_STATICRES = 60 * 10;
	
	/**
	 * 文件上传时，boundary值的最大长度
	 */
	public static final int MAX_LENGTH_OF_BOUNDARY = 256;
	
	/**
	 * 文件上传时，头部的最大长度
	 */
	public static final int MAX_LENGTH_OF_MULTI_HEADER = 128;
	
	/**
	 * 文件上传时，体的最大长度
	 */
	public static final int MAX_LENGTH_OF_MULTI_BODY = 1024 * 1024 * 20;

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	private String bindIp = null;//"127.0.0.1";

	/**
	 * 监听端口
	 */
	private Integer bindPort = 80;

	private String serverInfo = HttpConst.SERVER_INFO;

	private String charset = HttpConst.CHARSET_NAME;

	private ICache sessionStore = null;

	/**
	 * 存放HttpSession对象的cacheName

	 */
	private String sessionCacheName = SESSION_CACHE_NAME;

	/**
	 * session超时时间，单位：秒
	 */
	private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	private String sessionCookieName = SESSION_COOKIE_NAME;

	/**
	 * 静态资源缓存时间，如果小于等于0则不缓存，单位：秒
	 */
	private int maxLiveTimeOfStaticRes = MAX_LIVETIME_OF_STATICRES;

	private String page404 = "/404.html";

	//	private HttpSessionManager httpSessionManager;

	private String page500 = "/500.html";

	private ISessionIdGenerator sessionIdGenerator;
	
	private IHttpRequestHandler httpRequestHandler;

	/**
	 * 示例：
	 * 1、classpath中：classpath:page
	 * 2、绝对路径：/page
	 */
	private String root = null;//FileUtil.getAbsolutePath("classpath:page");//"/page";

	//	/**
	//	 * @return the httpSessionManager
	//	 */
	//	public HttpSessionManager getHttpSessionManager() {
	//		return httpSessionManager;
	//	}
	//
	//	/**
	//	 * @param httpSessionManager the httpSessionManager to set
	//	 */
	//	public void setHttpSessionManager(HttpSessionManager httpSessionManager) {
	//		this.httpSessionManager = httpSessionManager;
	//	}

	/**
	 *
	 * @author tanyaowu
	 */
	public HttpConfig(Integer bindPort, Long sessionTimeout) {
		this.bindPort = bindPort;
		if (sessionTimeout != null) {
			this.sessionTimeout = sessionTimeout;
		}
	}

	//	private File rootFile = null;

	/**
	 * @return the bindIp
	 */
	public String getBindIp() {
		return bindIp;
	}

	/**
	 * @return the bindPort
	 */
	public Integer getBindPort() {
		return bindPort;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the maxLiveTimeOfStaticRes
	 */
	public int getMaxLiveTimeOfStaticRes() {
		return maxLiveTimeOfStaticRes;
	}

	public String getPage404() {
		return page404;
	}

	public String getPage500() {
		return page500;
	}

	/**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @return the serverInfo
	 */
	public String getServerInfo() {
		return serverInfo;
	}

	/**
	 * @return the sessionCacheName
	 */
	public String getSessionCacheName() {
		return sessionCacheName;
	}

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	//	public void setSessionTimeout(long sessionTimeout) {
	//		this.sessionTimeout = sessionTimeout;
	//	}

	public ISessionIdGenerator getSessionIdGenerator() {
		return sessionIdGenerator;
	}

	public ICache getSessionStore() {
		return sessionStore;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param bindIp the bindIp to set
	 */
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param maxLiveTimeOfStaticRes the maxLiveTimeOfStaticRes to set
	 */
	public void setMaxLiveTimeOfStaticRes(int maxLiveTimeOfStaticRes) {
		this.maxLiveTimeOfStaticRes = maxLiveTimeOfStaticRes;
	}

	public void setPage404(String page404) {
		this.page404 = page404;
	}

	public void setPage500(String page500) {
		this.page500 = page500;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(String root) {
		this.root = FileUtil.getAbsolutePath(root);//"/page";;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	/**
	 * @param sessionCacheName the sessionCacheName to set
	 */
	public void setSessionCacheName(String sessionCacheName) {
		this.sessionCacheName = sessionCacheName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setSessionIdGenerator(ISessionIdGenerator sessionIdGenerator) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	public void setSessionStore(ICache sessionStore) {
		this.sessionStore = sessionStore;
		//		this.httpSessionManager = HttpSessionManager.getInstance(sessionStore);
	}

	/**
	 * @return the httpRequestHandler
	 */
	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	/**
	 * @param httpRequestHandler the httpRequestHandler to set
	 */
	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
}
