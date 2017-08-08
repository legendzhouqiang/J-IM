package org.tio.http.server.session;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class HttpSession {
	private IHttpSession proxy = null;
	
	private String sessionId = null;
	
	/**
	 * @author: tanyaowu
	 */
	public HttpSession(IHttpSession proxy, String sessionId) {
		this.proxy = proxy;
		this.sessionId = sessionId;
	}
	

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	public void setAtrribute(String key, Object value) {
		proxy.setAtrribute(key, value);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	public Object getAtrribute(String key) {
		return proxy.getAtrribute(key);
	}
	
	/**
	 * 清空所有属性
	 * 
	 * @author: tanyaowu
	 */
	public void clear(){
		proxy.clear();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
