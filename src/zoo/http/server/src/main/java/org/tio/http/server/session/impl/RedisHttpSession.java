package org.tio.http.server.session.impl;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.tio.http.server.session.IHttpSession;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class RedisHttpSession implements IHttpSession {
	private RedissonClient redisson;
	private Long sessionTimeout;//单位秒
	private String sessionId;
	RMapCache<String, Object> mapCache = null;

	/**
	 * @author: tanyaowu
	 */
	public RedisHttpSession(String sessionId, Long sessionTimeout, RedissonClient redisson) {
		this.sessionTimeout = sessionTimeout;
		this.redisson = redisson;
		this.sessionId = sessionId;
		mapCache = this.redisson.getMapCache(this.sessionId);
	}

	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	@Override
	public void setAtrribute(String key, Object value) {
		mapCache.put(key, value, 0, TimeUnit.SECONDS, sessionTimeout, TimeUnit.SECONDS);
	}

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public Object getAtrribute(String key) {
		return mapCache.get(key);
	}

	/**
	 * 清空所有属性
	 * 
	 * @author: tanyaowu
	 */
	@Override
	public void clear() {
		mapCache.clear();
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
