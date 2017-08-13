package org.tio.utils.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author tanyaowu 
 * 2017年8月10日 下午1:35:01
 */
public class RedisCache implements ICache {
	private static Logger log = LoggerFactory.getLogger(RedisCache.class);
	private static Map<String, RedisCache> map = new HashMap<>();
	private RedissonClient redisson = null;
	private String cacheName = null;
	private Long expireAfterWrite = null;
	private Long expireAfterAccess = null;
	private Long timeout = null;

	private RedisCache(RedissonClient redisson, String cacheName, Long expireAfterWrite, Long expireAfterAccess) {
		this.redisson = redisson;
		this.cacheName = cacheName;
		this.expireAfterWrite = expireAfterWrite;
		this.expireAfterAccess = expireAfterAccess;
		this.timeout = this.expireAfterWrite == null ? this.expireAfterAccess : this.expireAfterWrite;
		
	}

	/**
	 * expireAfterWrite和expireAfterAccess不允许同时为null
	 * @param cacheName
	 * @param expireAfterWrite
	 * @param expireAfterAccess
	 * @return
	 * @author: tanyaowu
	 */
	public static RedisCache register(RedissonClient redisson, String cacheName, Long expireAfterWrite, Long expireAfterAccess) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			synchronized (RedisCache.class) {
				redisCache = map.get(cacheName);
				if (redisCache == null) {
					redisCache = new RedisCache(redisson, cacheName, expireAfterWrite, expireAfterAccess);
					map.put(cacheName, redisCache);
				}
			}
		}
		return redisCache;
	}

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(redisson, cacheName, expireAfterWrite, expireAfterAccess)", cacheName, RedisCache.class.getSimpleName());
		}
		return redisCache;
	}
	
	private String keyPrefix() {
		return "{" + cacheName + "}";
	}
	
	private String cacheKey(String id) {
		return keyPrefix() + id;
	}

	@Override
	public void put(String key, Serializable value) {
		key = cacheKey(key);
		RBucket<Serializable> bucket = redisson.getBucket(key);
		bucket.set(value, timeout, TimeUnit.SECONDS);
	}

	@Override
	public void remove(String key) {
		key = cacheKey(key);
		RBucket<Serializable> bucket = redisson.getBucket(key);
		bucket.delete();
	}

	@Override
	public Serializable get(String key) {
		key = cacheKey(key);
		RBucket<Serializable> bucket = redisson.getBucket(key);
		Serializable ret = bucket.get();
		if (expireAfterAccess != null) {
			bucket.expire(timeout, TimeUnit.SECONDS);
		}
		return ret;
	}
	
	@Override
	public Collection<String> keys() {
		RKeys keys = redisson.getKeys();
		Collection<String> allkey = keys.findKeysByPattern(keyPrefix() + "*");
		return allkey;
	}
	
	@Override
	public void clear() {
		RKeys keys = redisson.getKeys();
		keys.deleteByPattern(keyPrefix() + "*");
	}
	
	public static void main(String[] args) {}
}
