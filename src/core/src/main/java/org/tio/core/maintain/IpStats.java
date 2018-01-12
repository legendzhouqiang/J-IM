package org.tio.core.maintain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.GroupContext;
import org.tio.core.cache.IpStatRemovalListener;
import org.tio.core.intf.IpStatListener;
import org.tio.core.stat.IpStat;
import org.tio.utils.cache.caffeine.CaffeineCache;

/**
 *
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class IpStats {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(IpStats.class);

	private final static String CACHE_NAME = "TIO_IP_STAT";
	//	private final static Long timeToLiveSeconds = null;
	//	private final static Long timeToIdleSeconds = Time.DAY_1;

	private String groupContextId;
	private GroupContext groupContext;

	//	private CaffeineCache[] caches = null;
	/**
	 * key: 时长，单位：秒
	 */
	public final Map<Long, CaffeineCache> cacheMap = new HashMap<>();

	public final List<Long> durationList = new ArrayList<>();

	public IpStats(GroupContext groupContext, IpStatListener ipStatListener, Long[] durations) {
		this.groupContext = groupContext;
		this.groupContextId = groupContext.getId();
		if (durations != null) {
//			for (Long duration : durations) {
//				addDuration(duration, ipStatListener);
//			}
			addDurations(durations, ipStatListener);
		}
	}

	/**
	 * 添加监控时段
	 * @param duration 单位：秒
	 * @param ipStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addDuration(Long duration, IpStatListener ipStatListener) {
		@SuppressWarnings("unchecked")
		CaffeineCache caffeineCache = CaffeineCache.register(getCacheName(duration), duration, null, new IpStatRemovalListener(groupContext, ipStatListener));
		cacheMap.put(duration, caffeineCache);
		durationList.add(duration);
	}

	/**
	 * 添加监控时段
	 * @param durations 单位：秒
	 * @param ipStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addDurations(Long[] durations, IpStatListener ipStatListener) {
		if (durations != null) {
			for (Long duration : durations) {
				addDuration(duration, ipStatListener);
			}
		}
	}

	/**
	 * 删除监控时间段
	 * @param duration
	 * @author: tanyaowu
	 */
	public void removeDuration(Long duration) {
		clear(duration);
		cacheMap.remove(duration);
		durationList.remove(duration);
	}

	/**
	 * 
	 * @param duration
	 * @return
	 * @author: tanyaowu
	 */
	public String getCacheName(Long duration) {
		String cacheName = CACHE_NAME + "_" + this.groupContextId + "_";
		return cacheName + duration;
	}

	/**
	 * 清空监控数据
	 * @author: tanyaowu
	 */
	public void clear(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return;
		}
		caffeineCache.clear();
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则创建
	 * @param duration
	 * @param ip
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long duration, String ip) {
		return get(duration, ip, true);
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param duration
	 * @param ip
	 * @param forceCreate
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long duration, String ip, boolean forceCreate) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}

		IpStat ipStat = (IpStat) caffeineCache.get(ip);
		if (ipStat == null && forceCreate) {
			synchronized (this) {
				ipStat = (IpStat) caffeineCache.get(ip);
				if (ipStat == null) {
					ipStat = new IpStat(ip, duration);
					caffeineCache.put(ip, ipStat);
				}
			}
		}
		return ipStat;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public ConcurrentMap<String, Serializable> map(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		ConcurrentMap<String, Serializable> map = caffeineCache.asMap();
		return map;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Long size(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		return caffeineCache.size();
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Collection<Serializable> values(Long duration) {
		CaffeineCache caffeineCache = cacheMap.get(duration);
		if (caffeineCache == null) {
			return null;
		}
		Collection<Serializable> set = caffeineCache.asMap().values();
		return set;
	}

}
