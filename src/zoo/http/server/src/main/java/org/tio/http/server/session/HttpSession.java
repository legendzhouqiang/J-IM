package org.tio.http.server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class HttpSession implements java.io.Serializable {
	
	private static final long serialVersionUID = 6077020620501316538L;

	private Map<String, Object> map = new ConcurrentHashMap<>();
	
	private String sessionId = null;
	
	/**
	 * @author: tanyaowu
	 */
	public HttpSession(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public HttpSession() {
	}
	

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	public void setAtrribute(String key, Object value) {
		map.put(key, value);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	public Object getAtrribute(String key) {
		return map.get(key);
	}
	
	/**
	 * 清空所有属性
	 * 
	 * @author: tanyaowu
	 */
	public void clear(){
		map.clear();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
