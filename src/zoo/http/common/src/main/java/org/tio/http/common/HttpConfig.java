package org.tio.http.common;

import org.tio.utils.cache.ICache;

import com.xiaoleilu.hutool.io.FileUtil;

/**
 * @author tanyaowu 
 * 2017年8月15日 下午1:21:14
 */
public class HttpConfig {
	
//	private static Logger log = LoggerFactory.getLogger(HttpConfig.class);

	private String bindIp = null;//"127.0.0.1";

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
	 * 监听端口
	 */
	private Integer bindPort = 80;

	private String serverInfo = HttpConst.SERVER_INFO;

	private String charset = HttpConst.CHARSET_NAME;

	private ICache httpSessionStore = null;

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
	 * 静态资源缓存时间，单位：秒
	 */
	private int maxLiveTimeOfStaticRes = MAX_LIVETIME_OF_STATICRES;


	/**
	 * 示例：
	 * 1、classpath中：classpath:page
	 * 2、绝对路径：/page
	 */
	private String root = null;//FileUtil.getAbsolutePath("classpath:page");//"/page";

	//	private File rootFile = null;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public HttpConfig(Integer bindPort, Long sessionTimeout) {
		this.bindPort = bindPort;
		if (sessionTimeout != null) {
			this.sessionTimeout = sessionTimeout;
		}
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the bindIp
	 */
	public String getBindIp() {
		return bindIp;
	}

	/**
	 * @param bindIp the bindIp to set
	 */
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
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
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * @param root the root to set
	 */
	public void setRoot(String root) {
		this.root = FileUtil.getAbsolutePath(root);//"/page";;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	//	public void setSessionTimeout(long sessionTimeout) {
	//		this.sessionTimeout = sessionTimeout;
	//	}

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public ICache getHttpSessionStore() {
		return httpSessionStore;
	}

	public void setHttpSessionStore(ICache httpSessionStore) {
		this.httpSessionStore = httpSessionStore;
	}

	/**
	 * @return the sessionCacheName
	 */
	public String getSessionCacheName() {
		return sessionCacheName;
	}

	/**
	 * @param sessionCacheName the sessionCacheName to set
	 */
	public void setSessionCacheName(String sessionCacheName) {
		this.sessionCacheName = sessionCacheName;
	}



	/**
	 * @return the serverInfo
	 */
	public String getServerInfo() {
		return serverInfo;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	/**
	 * @return the maxLiveTimeOfStaticRes
	 */
	public int getMaxLiveTimeOfStaticRes() {
		return maxLiveTimeOfStaticRes;
	}

	/**
	 * @param maxLiveTimeOfStaticRes the maxLiveTimeOfStaticRes to set
	 */
	public void setMaxLiveTimeOfStaticRes(int maxLiveTimeOfStaticRes) {
		this.maxLiveTimeOfStaticRes = maxLiveTimeOfStaticRes;
	}


}
