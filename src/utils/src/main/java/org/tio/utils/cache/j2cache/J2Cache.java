/**
 * 
 */
package org.tio.utils.cache.j2cache;

import java.io.Serializable;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.ICache;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

/**
 * 红薯家的j2cache
 * @author tanyw
 *
 */
public class J2Cache implements ICache {
	private static Logger log = LoggerFactory.getLogger(J2Cache.class);

	private String cacheName = null;

	/**
	 * 
	 */
	public J2Cache(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private static CacheChannel getChannel() {
		CacheChannel cache = J2Cache.getChannel();
		return cache;
	}

	@Override
	public void clear() {
		CacheChannel cache = getChannel();
		cache.clear(cacheName);
	}

	@Override
	public Serializable get(String key) {
		CacheChannel cache = getChannel();
		CacheObject cacheObject = cache.get(cacheName, key);
		if (cacheObject != null) {
			return (Serializable) cacheObject.getValue();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		Serializable ret = get(key);
		return (T) ret;
	}

	@Override
	public Collection<String> keys() {
		CacheChannel cache = getChannel();
		return cache.keys(cacheName);
	}

	@Override
	public void put(String key, Serializable value) {
		CacheChannel cache = getChannel();
		cache.set(cacheName, key, value);
	}

	@Override
	public void remove(String key) {
		CacheChannel cache = getChannel();
		cache.evict(cacheName, key);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		throw new RuntimeException("不支持缓存穿透");

	}

}
