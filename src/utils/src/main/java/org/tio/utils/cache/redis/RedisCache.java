package org.tio.utils.cache.redis;

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
import org.tio.utils.cache.ICache;

/**
 *
 * @author tanyaowu
 * 2017年8月10日 下午1:35:01
 */
public class RedisCache implements ICache {
	private static Logger log = LoggerFactory.getLogger(RedisCache.class);
	private static Map<String, RedisCache> map = new HashMap<>();

	public static String cacheKey(String cacheName, String key) {
		return keyPrefix(cacheName) + key;
	}

	public static RedisCache getCache(String cacheName) {
		RedisCache redisCache = map.get(cacheName);
		if (redisCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(redisson, cacheName, expireAfterWrite, expireAfterAccess)", cacheName, RedisCache.class.getSimpleName());
		}
		return redisCache;
	}

	public static String keyPrefix(String cacheName) {
		return "{" + cacheName + "}";
	}

	public static void main(String[] args) {
	}

	/**
	 * expireAfterWrite和expireAfterAccess不允许同时为null
	 * @param cacheName
	 * @param expireAfterWrite
	 * @param expireAfterAccess
	 * @return
	 * @author tanyaowu
	 */
	public static RedisCache register(RedissonClient redisson, String cacheName, Long expireAfterWrite, Long expireAfterAccess) {
		RedisTask.start();

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

	@Override
	public void clear() {
		RKeys keys = redisson.getKeys();
		keys.deleteByPattern(keyPrefix(cacheName) + "*");
	}

	@Override
	public Serializable get(String key) {
		RBucket<Serializable> bucket = getBucket(key);
		Serializable ret = bucket.get();
		if (expireAfterAccess != null) {
			//			bucket.expire(timeout, TimeUnit.SECONDS);
			RedisTask.add(cacheName, key, timeout);
		}
		return ret;
	}

	public RBucket<Serializable> getBucket(String key) {
		key = cacheKey(cacheName, key);
		RBucket<Serializable> bucket = redisson.getBucket(key);
		return bucket;
	}

	public String getCacheName() {
		return cacheName;
	}

	public Long getExpireAfterAccess() {
		return expireAfterAccess;
	}

	public Long getExpireAfterWrite() {
		return expireAfterWrite;
	}

	public RedissonClient getRedisson() {
		return redisson;
	}

	public Long getTimeout() {
		return timeout;
	}

	@Override
	public Collection<String> keys() {
		RKeys keys = redisson.getKeys();
		Collection<String> allkey = keys.findKeysByPattern(keyPrefix(cacheName) + "*");
		return allkey;
	}

	@Override
	public void put(String key, Serializable value) {
		RBucket<Serializable> bucket = getBucket(key);
		bucket.set(value, timeout, TimeUnit.SECONDS);
	}

	@Override
	public void remove(String key) {
		RBucket<Serializable> bucket = getBucket(key);
		bucket.delete();
	}
}
