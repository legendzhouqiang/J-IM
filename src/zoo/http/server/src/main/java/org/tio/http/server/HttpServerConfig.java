package org.tio.http.server;

import org.tio.http.common.Const;
import org.tio.http.common.HttpConst;
import org.tio.http.server.session.IHttpSessionStore;

import com.xiaoleilu.hutool.io.FileUtil;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午2:42:59
 */
public class HttpServerConfig {

	private String bindIp = null;//"127.0.0.1";
	
	/**
	 * 默认的超时时间，单位：秒
	 */
	public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60;

	private Integer bindPort = 2046;

	private String charset = HttpConst.CHARSET_NAME;

	private IHttpSessionStore httpSessionStore = null;

	/**
	 * session超时时间，单位：秒
	 */
	private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	private String sessionCookieName = Const.SESSION_COOKIE_NAME;

	/**
	 * 示例：
	 * 1、classpath中：classpath:page
	 * 2、绝对路径：/page
	 */
	private String root = FileUtil.getAbsolutePath("classpath:page");//"/page";

	//	private File rootFile = null;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public HttpServerConfig(Integer bindPort, Long sessionTimeout) {
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

	public IHttpSessionStore getHttpSessionStore() {
		return httpSessionStore;
	}

	public void setHttpSessionStore(IHttpSessionStore httpSessionStore) {
		this.httpSessionStore = httpSessionStore;
	}

}
