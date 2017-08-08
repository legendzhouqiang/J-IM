package org.tio.http.server.session.impl;

import java.util.HashMap;
import java.util.Map;

import org.tio.http.server.session.IHttpSession;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class HashMapHttpSession implements IHttpSession {
	private Map<String, Object> props = new HashMap<>();

	/**
	 * @author: tanyaowu
	 */
	public HashMapHttpSession() {
	}

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	@Override
	public void setAtrribute(String key, Object value) {
		props.put(key, value);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public Object getAtrribute(String key) {
		return props.get(key);
	}

	/**
	 * 清空所有属性
	 * 
	 * @author: tanyaowu
	 */
	@Override
	public void clear() {
		props.clear();
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月21日 上午10:27:54
	 * 
	 */
	public static void main(String[] args) {

	}

}
