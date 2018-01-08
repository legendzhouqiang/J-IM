/**
 * 
 */
package org.tio.utils.cache.j2cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.ICache;

import net.oschina.j2cache.CacheChannel;

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
	public static void main(String[] args) {}
	
	private static CacheChannel getChannel() {
		CacheChannel cache = J2Cache.getChannel();
		return cache;
	}
	

	@Override
	public void clear() {
		CacheChannel cache = getChannel();
		try {
			cache.clear(cacheName);
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Serializable get(String key) {
		CacheChannel cache = getChannel();
		try {
			return cache.getRawObject(cacheName, key);
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		CacheChannel cache = getChannel();
		try {
			Serializable ret =  cache.getRawObject(cacheName, key);
			return (T)ret;
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<String> keys() {
		CacheChannel cache = getChannel();
		try {
			return cache.keys(cacheName);
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void put(String key, Serializable value) {
		CacheChannel cache = getChannel();
		try {
			cache.set(cacheName, key, value);
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(String key) {
		CacheChannel cache = getChannel();
		try {
			cache.evict(cacheName, key);
		} catch (IOException e) {
			log.error(e.toString(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		throw new RuntimeException("不支持缓存穿透");
		
	}

}
