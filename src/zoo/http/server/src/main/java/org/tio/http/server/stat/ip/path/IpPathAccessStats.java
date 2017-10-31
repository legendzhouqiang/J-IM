package org.tio.http.server.stat.ip.path;

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
import org.tio.utils.cache.guava.GuavaCache;

/**
 * 
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class IpPathAccessStats {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(IpPathAccessStats.class);

	private final static String CACHE_NAME = "TIO_IP_ACCESSPATH";
	//	private final static Long timeToLiveSeconds = null;
	//	private final static Long timeToIdleSeconds = Time.DAY_1;

	private GroupContext groupContext;
	
	private String groupContextId;

	//	private GuavaCache[] caches = null;
	/**
	 * key:   时长段，单位：秒
	 * value: GuavaCache: key: ip, value: IpAccessStat
	 */
	public final Map<Long, GuavaCache> cacheMap = new HashMap<>();

	/**
	 * 时长段列表
	 */
	public final List<Long> durationList = new ArrayList<>();
	
	private final Map<Long, IpPathAccessStatListener> listenerMap = new HashMap<>();

	/**
	 * 
	 * @param groupContext
	 * @param ipPathAccessStatListener
	 * @param durations
	 * @author tanyaowu
	 */
	public IpPathAccessStats(GroupContext groupContext, IpPathAccessStatListener ipPathAccessStatListener, Long[] durations) {
		this.groupContext = groupContext;
		this.groupContextId = groupContext.getId();
		if (durations != null) {
			for (Long duration : durations) {
				addDuration(duration, ipPathAccessStatListener);
			}
		}
	}

	/**
	 * 添加监控时段
	 * @param duration 单位：秒
	 * @param ipPathAccessStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addDuration(Long duration, IpPathAccessStatListener ipPathAccessStatListener) {
		@SuppressWarnings("unchecked")
		GuavaCache guavaCache = GuavaCache.register(getCacheName(duration), duration, null, new IpPathAccessStatRemovalListener(groupContext, ipPathAccessStatListener));
		cacheMap.put(duration, guavaCache);
		durationList.add(duration);
		
		if (ipPathAccessStatListener != null) {
			listenerMap.put(duration, ipPathAccessStatListener);
		}
	}
	
	/**
	 * 
	 * @param duration
	 * @return
	 * @author tanyaowu
	 */
	public IpPathAccessStatListener getListener(Long duration) {
		return listenerMap.get(duration);
	}

	/**
	 * 添加监控时段
	 * @param durations 单位：秒
	 * @param ipPathAccessStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addDurations(Long[] durations, IpPathAccessStatListener ipPathAccessStatListener) {
		if (durations != null) {
			for (Long duration : durations) {
				addDuration(duration, ipPathAccessStatListener);
			}
		}
	}

	/**
	 * 删除监控时间段
	 * @param duration
	 * @author: tanyaowu
	 */
	public void removeMonitor(Long duration) {
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
		GuavaCache guavaCache = cacheMap.get(duration);
		if (guavaCache == null) {
			return;
		}
		guavaCache.clear();
	}

	
	
	/**
	 * 获取IpAccessStat
	 * @param duration
	 * @param ip
	 * @param forceCreate
	 * @return
	 * @author tanyaowu
	 */
	public IpAccessStat get(Long duration, String ip, boolean forceCreate) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		
		GuavaCache guavaCache = cacheMap.get(duration);
		if (guavaCache == null) {
			return null;
		}

		IpAccessStat ipAccessStat = (IpAccessStat) guavaCache.get(ip);
		if (ipAccessStat == null && forceCreate) {
			synchronized (guavaCache) {
				ipAccessStat = (IpAccessStat) guavaCache.get(ip);
				if (ipAccessStat == null) {
					ipAccessStat = new IpAccessStat(duration, ip);//new MapWithLock<String, IpPathAccessStat>(new HashMap<>());//new IpPathAccessStat(duration, ip, path);
					guavaCache.put(ip, ipAccessStat);
				}
			}
		}
		
		return ipAccessStat;
	}
	
	/**
	 * 获取IpAccessStat
	 * @param duration
	 * @param ip
	 * @return
	 * @author tanyaowu
	 */
	public IpAccessStat get(Long duration, String ip) {
		return get(duration, ip, true);
	}

	/**
	 * key:   ip
	 * value: IpPathAccessStat
	 * @param duration
	 * @return
	 * @author tanyaowu
	 */
	public ConcurrentMap<String, Serializable> map(Long duration) {
		GuavaCache guavaCache = cacheMap.get(duration);
		if (guavaCache == null) {
			return null;
		}
		ConcurrentMap<String, Serializable> map = guavaCache.asMap();
		return map;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Long size(Long duration) {
		GuavaCache guavaCache = cacheMap.get(duration);
		if (guavaCache == null) {
			return null;
		}
		return guavaCache.size();
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Collection<Serializable> values(Long duration) {
		GuavaCache guavaCache = cacheMap.get(duration);
		if (guavaCache == null) {
			return null;
		}
		Collection<Serializable> set = guavaCache.asMap().values();
		return set;
	}

}
