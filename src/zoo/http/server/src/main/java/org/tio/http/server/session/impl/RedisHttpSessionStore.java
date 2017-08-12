package org.tio.http.server.session.impl;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.session.IHttpSessionStore;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class RedisHttpSessionStore implements IHttpSessionStore {

	private RedissonClient redisson;
	
	private Long sessionTimeout = null;
	
	public static final String cacheName = "tio-s";

//	private RMapCache<String, HttpSession> cachedMap = null;
	
	private static RedisHttpSessionStore instance;

	public static RedisHttpSessionStore getInstance(RedissonClient redisson, Long sessionTimeout) {
		if (instance == null) {
			synchronized (RedisHttpSessionStore.class) {
				if (instance == null) {
					instance = new RedisHttpSessionStore(redisson, sessionTimeout);
				}
			}
		}
		return instance;
	}
	
//	@SuppressWarnings("unchecked")
	private RedisHttpSessionStore(RedissonClient redisson, Long sessionTimeout) {
		this.redisson = redisson;
		this.sessionTimeout = sessionTimeout;

	}
	
	private String cacheKey(String sessionId) {
		return "{" + cacheName + "}" + sessionId;
	}
	
	@Override
	public void save(String sessionId, HttpSession session) {
		String key = cacheKey(sessionId);
		RBucket<HttpSession> bucket = redisson.getBucket(key);
		bucket.set(session, sessionTimeout, TimeUnit.SECONDS);
	}

	@Override
	public void remove(String sessionId) {
		String key = cacheKey(sessionId);
		RBucket<HttpSession> bucket = redisson.getBucket(key);
		bucket.delete();
	}

	@Override
	public HttpSession get(String sessionId) {
		String key = cacheKey(sessionId);
		RBucket<HttpSession> bucket = redisson.getBucket(key);
		HttpSession ret = bucket.get();
		if (ret != null) {
			bucket.expire(sessionTimeout, TimeUnit.SECONDS);
		}
		return ret;
	}
	
}
