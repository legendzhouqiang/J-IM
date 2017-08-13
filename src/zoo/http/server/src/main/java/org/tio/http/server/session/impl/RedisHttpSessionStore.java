package org.tio.http.server.session.impl;

import org.redisson.api.RedissonClient;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.session.IHttpSessionStore;
import org.tio.utils.cache.RedisCache;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class RedisHttpSessionStore implements IHttpSessionStore {
	
	public static final String HTTP_SESSION_CACHE_NAME = "tio-http-session";

//	private RMapCache<String, HttpSession> cachedMap = null;
	
	private static RedisHttpSessionStore instance;
	
	private RedisCache redisCache;

	public static RedisHttpSessionStore getInstance(RedissonClient redisson, Long sessionTimeout) {
		if (instance == null) {
			synchronized (RedisHttpSessionStore.class) {
				if (instance == null) {
					RedisCache redisCache = RedisCache.register(redisson, HTTP_SESSION_CACHE_NAME, null, sessionTimeout);
					instance = new RedisHttpSessionStore(redisCache);
				}
			}
		}
		return instance;
	}
	
	private RedisHttpSessionStore(RedisCache redisCache) {
		this.redisCache = redisCache;

	}

	@Override
	public void save(String sessionId, HttpSession session) {
		redisCache.put(sessionId, session);;
	}

	@Override
	public void remove(String sessionId) {
		redisCache.remove(sessionId);
	}

	@Override
	public HttpSession get(String sessionId) {
		return (HttpSession)redisCache.get(sessionId);
	}
	
}
