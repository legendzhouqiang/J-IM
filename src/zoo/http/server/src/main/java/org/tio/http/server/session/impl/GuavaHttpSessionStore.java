package org.tio.http.server.session.impl;

import org.tio.core.utils.GuavaUtils;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.session.IHttpSessionStore;

import com.google.common.cache.LoadingCache;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午10:16:26
 */
public class GuavaHttpSessionStore implements IHttpSessionStore {
	private LoadingCache<String, HttpSession> loadingCache = null;

	private GuavaHttpSessionStore(Long sessionTimeout) {
		Integer concurrencyLevel = 8;
		Long expireAfterWrite = null;
		Long expireAfterAccess = sessionTimeout;
		Integer initialCapacity = 10;
		Integer maximumSize = 1000000000;
		boolean recordStats = false;
		loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, expireAfterWrite, expireAfterAccess, initialCapacity, maximumSize, recordStats);
	}

	private static GuavaHttpSessionStore instance;

	public static GuavaHttpSessionStore getInstance(Long sessionTimeout) {
		if (instance == null) {
			synchronized (GuavaHttpSessionStore.class) {
				if (instance == null) {
					instance = new GuavaHttpSessionStore(sessionTimeout);
				}
			}
		}
		return instance;
	}

	@Override
	public void save(String sessionId, HttpSession session) {
		loadingCache.put(sessionId, session);
	}

	@Override
	public void remove(String sessionId) {
		loadingCache.invalidate(sessionId);
	}

	@Override
	public HttpSession get(String sessionId) {
		return loadingCache.getIfPresent(sessionId);
	}

}
