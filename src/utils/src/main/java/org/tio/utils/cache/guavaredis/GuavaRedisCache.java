package org.tio.utils.cache.guavaredis;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.ICache;
import org.tio.utils.cache.guava.GuavaCache;
import org.tio.utils.cache.redis.RedisCache;
import org.tio.utils.cache.redis.RedisTask;

/**
 * @author tanyaowu
 * 2017年8月12日 下午9:13:54
 */
public class GuavaRedisCache implements ICache {

	public static final String CACHE_CHANGE_TOPIC = "TIO_CACHE_CHANGE_TOPIC";

	private static Logger log = LoggerFactory.getLogger(GuavaRedisCache.class);
	public static Map<String, GuavaRedisCache> map = new HashMap<>();

	static RTopic<CacheChangedVo> topic;

	private static boolean inited = false;

	public static GuavaRedisCache getCache(String cacheName) {
		GuavaRedisCache guavaRedisCache = map.get(cacheName);
		if (guavaRedisCache == null) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, GuavaRedisCache.class.getSimpleName());
		}
		return guavaRedisCache;
	}

	private static void init(RedissonClient redisson) {
		if (!inited) {
			synchronized (GuavaRedisCache.class) {
				if (!inited) {
					topic = redisson.getTopic(CACHE_CHANGE_TOPIC);
					topic.addListener(new MessageListener<CacheChangedVo>() {
						@Override
						public void onMessage(String channel, CacheChangedVo cacheChangedVo) {
							String clientid = cacheChangedVo.getClientId();
							if (Objects.equals(CacheChangedVo.CLIENTID, clientid)) {
								log.info("自己发布的消息");
								return;
							}

							String cacheName = cacheChangedVo.getCacheName();
							GuavaRedisCache guavaRedisCache = GuavaRedisCache.getCache(cacheName);
							if (guavaRedisCache == null) {
								return;
							}

							CacheChangeType type = cacheChangedVo.getType();
							if (type == CacheChangeType.PUT || type == CacheChangeType.UPDATE || type == CacheChangeType.REMOVE) {
								String key = cacheChangedVo.getKey();
								guavaRedisCache.guavaCache.remove(key);
							} else if (type == CacheChangeType.CLEAR) {
								guavaRedisCache.guavaCache.clear();
							}
						}
					});
					inited = true;
				}
			}
		}
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	public static GuavaRedisCache register(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		init(redisson);

		GuavaRedisCache guavaRedisCache = map.get(cacheName);
		if (guavaRedisCache == null) {
			synchronized (GuavaRedisCache.class) {
				guavaRedisCache = map.get(cacheName);
				if (guavaRedisCache == null) {
					RedisCache redisCache = RedisCache.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);
					GuavaCache guavaCache = GuavaCache.register(cacheName, timeToLiveSeconds, timeToIdleSeconds);

					guavaRedisCache = new GuavaRedisCache(cacheName, guavaCache, redisCache);
					map.put(cacheName, guavaRedisCache);
				}
			}
		}
		return guavaRedisCache;
	}

	GuavaCache guavaCache;

	RedisCache redisCache;

	String cacheName;

	/**
	 *
	 * @author tanyaowu
	 */
	public GuavaRedisCache() {
	}

	/**
	 * @param guavaCache
	 * @param redisCache
	 * @author tanyaowu
	 */
	public GuavaRedisCache(String cacheName, GuavaCache guavaCache, RedisCache redisCache) {
		super();
		this.cacheName = cacheName;
		this.guavaCache = guavaCache;
		this.redisCache = redisCache;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	@Override
	public void clear() {
		guavaCache.clear();
		redisCache.clear();

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, CacheChangeType.CLEAR);
		topic.publish(cacheChangedVo);
	}

	/**
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Serializable get(String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		
		Serializable ret = guavaCache.get(key);
		if (ret == null) {
			ret = redisCache.get(key);
			if (ret != null) {
				guavaCache.put(key, ret);
			}
		} else {
			Long timeToIdleSeconds = redisCache.getTimeToIdleSeconds();
			if (timeToIdleSeconds != null) {
				RedisTask.add(cacheName, key, timeToIdleSeconds);
			}
		}
		return ret;
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Collection<String> keys() {
		return redisCache.keys();
	}

	/**
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	@Override
	public void put(String key, Serializable value) {
		guavaCache.put(key, value);
		redisCache.put(key, value);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		topic.publish(cacheChangedVo);
	}

	/**
	 * @param key
	 * @author tanyaowu
	 */
	@Override
	public void remove(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		
		guavaCache.remove(key);
		redisCache.remove(key);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.REMOVE);
		topic.publish(cacheChangedVo);
	}
}
