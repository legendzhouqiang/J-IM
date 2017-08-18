package org.tio.utils.cache.guava;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.GuavaUtils;
import org.tio.utils.cache.ICache;

import com.google.common.cache.LoadingCache;

/**
 *
 * @author tanyaowu
 * 2017年8月5日 上午10:16:26
 */
public class GuavaCache implements ICache {
	private static Logger log = LoggerFactory.getLogger(GuavaCache.class);

	public static Map<String, GuavaCache> map = new HashMap<>();

	public static GuavaCache getCache(String cacheName) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, expireAfterWrite, expireAfterAccess)", cacheName, GuavaCache.class.getSimpleName());
		}
		return guavaCache;
	}

	/**
	 * expireAfterWrite和expireAfterAccess不允许同时为null
	 * @param cacheName
	 * @param expireAfterWrite
	 * @param expireAfterAccess
	 * @return
	 * @author tanyaowu
	 */
	public static GuavaCache register(String cacheName, Long expireAfterWrite, Long expireAfterAccess) {
		GuavaCache guavaCache = map.get(cacheName);
		if (guavaCache == null) {
			synchronized (GuavaCache.class) {
				guavaCache = map.get(cacheName);
				if (guavaCache == null) {
					Integer concurrencyLevel = 8;
					Integer initialCapacity = 10;
					Integer maximumSize = 1000000000;
					boolean recordStats = false;
					LoadingCache<String, Serializable> loadingCache = GuavaUtils.createLoadingCache(concurrencyLevel, expireAfterWrite, expireAfterAccess, initialCapacity,
							maximumSize, recordStats);
					guavaCache = new GuavaCache(loadingCache);
					map.put(cacheName, guavaCache);
				}
			}
		}
		return guavaCache;
	}

	private LoadingCache<String, Serializable> loadingCache = null;

	private GuavaCache(LoadingCache<String, Serializable> loadingCache) {
		this.loadingCache = loadingCache;
	}

	@Override
	public void clear() {
		loadingCache.invalidateAll();
	}

	@Override
	public Serializable get(String key) {
		return loadingCache.getIfPresent(key);
	}

	@Override
	public Collection<String> keys() {
		ConcurrentMap<String, Serializable> map = loadingCache.asMap();
		return map.keySet();
	}

	@Override
	public void put(String key, Serializable value) {
		loadingCache.put(key, value);
	}

	@Override
	public void remove(String key) {
		loadingCache.invalidate(key);
	}
}
