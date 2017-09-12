package org.tio.http.common.session;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tio.http.common.HttpConfig;

/**
 *
 * @author tanyaowu
 * 2017年8月5日 上午10:16:26
 */
public class HttpSession implements java.io.Serializable {

	private static final long serialVersionUID = 6077020620501316538L;

	private Map<String, Serializable> data = new ConcurrentHashMap<>();

	private String id = null;

	/**
	 * 此处空的构造函数必须要有
	 * 
	 * @author: tanyaowu
	 */
	public HttpSession() {
	}

	/**
	 * @author tanyaowu
	 */
	public HttpSession(String id) {
		this.id = id;
	}

	/**
	 * 清空所有属性
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void clear(HttpConfig httpConfig) {
		data.clear();
		httpConfig.getSessionStore().put(id, this);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public Object getAttribute(String key) {
		return data.get(key);
	}

	public Map<String, Serializable> getData() {
		return data;
	}

	public String getId() {
		return id;
	}

	/**
	 *
	 * @param key
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void removeAttribute(String key, HttpConfig httpConfig) {
		data.remove(key);
		httpConfig.getSessionStore().put(id, this);
	}

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @param httpConfig
	 * @author tanyaowu
	 */
	public void setAttribute(String key, Serializable value, HttpConfig httpConfig) {
		data.put(key, value);
		httpConfig.getSessionStore().put(id, this);
	}

	public void setData(Map<String, Serializable> data) {
		this.data = data;
	}

	public void setId(String id) {
		this.id = id;
	}
}
