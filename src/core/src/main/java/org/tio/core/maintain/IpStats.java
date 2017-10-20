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
import org.tio.utils.cache.guava.GuavaCache;

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

	//	private GuavaCache[] caches = null;
	/**
	 * key: 时长，单位：秒
	 */
	public final Map<Long, GuavaCache> map = new HashMap<>();

	public final List<Long> list = new ArrayList<>();

	public IpStats(GroupContext groupContext, IpStatListener ipStatListener, Long[] ipStatDurations) {
		this.groupContext = groupContext;
		this.groupContextId = groupContext.getId();
		if (ipStatDurations != null) {
			for (Long ipStatDuration : ipStatDurations) {
				addMonitor(ipStatDuration, ipStatListener);
			}
		}
	}

	/**
	 * 添加监控时段
	 * @param ipStatDuration 单位：秒
	 * @param ipStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addMonitor(Long ipStatDuration, IpStatListener ipStatListener) {
		@SuppressWarnings("unchecked")
		GuavaCache guavaCache = GuavaCache.register(getCacheName(ipStatDuration), ipStatDuration, null, new IpStatRemovalListener(groupContext, ipStatListener));
		map.put(ipStatDuration, guavaCache);
		list.add(ipStatDuration);
	}

	/**
	 * 添加监控时段
	 * @param ipStatDurations 单位：秒
	 * @param ipStatListener 可以为null
	 * @author: tanyaowu
	 */
	public void addMonitors(Long[] ipStatDurations, IpStatListener ipStatListener) {
		if (ipStatDurations != null) {
			for (Long ipStatDuration : ipStatDurations) {
				addMonitor(ipStatDuration, ipStatListener);
			}
		}
	}

	/**
	 * 删除监控时间段
	 * @param ipStatDuration
	 * @author: tanyaowu
	 */
	public void removeMonitor(Long ipStatDuration) {
		clear(ipStatDuration);
		map.remove(ipStatDuration);
		list.remove(ipStatDuration);
	}

	/**
	 * 
	 * @param ipStatDuration
	 * @return
	 * @author: tanyaowu
	 */
	public String getCacheName(Long ipStatDuration) {
		String cacheName = CACHE_NAME + "_" + this.groupContextId + "_";
		return cacheName + ipStatDuration;
	}

	/**
	 * 清空监控数据
	 * @author: tanyaowu
	 */
	public void clear(Long ipStatDuration) {
		GuavaCache guavaCache = map.get(ipStatDuration);
		if (guavaCache == null) {
			return;
		}
		guavaCache.clear();
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则创建
	 * @param ipStatDuration
	 * @param ip
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long ipStatDuration, String ip) {
		return get(ipStatDuration, ip, true);
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param ipStatDuration
	 * @param ip
	 * @param forceCreate
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(Long ipStatDuration, String ip, boolean forceCreate) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		GuavaCache guavaCache = map.get(ipStatDuration);
		if (guavaCache == null) {
			return null;
		}

		IpStat ipStat = (IpStat) guavaCache.get(ip);
		if (ipStat == null && forceCreate) {
			synchronized (this) {
				ipStat = (IpStat) guavaCache.get(ip);
				if (ipStat == null) {
					ipStat = new IpStat(ip, ipStatDuration);
					guavaCache.put(ip, ipStat);
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
	public ConcurrentMap<String, Serializable> map(Long ipStatDuration) {
		GuavaCache guavaCache = map.get(ipStatDuration);
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
	public Long size(Long ipStatDuration) {
		GuavaCache guavaCache = map.get(ipStatDuration);
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
	public Collection<Serializable> values(Long ipStatDuration) {
		GuavaCache guavaCache = map.get(ipStatDuration);
		if (guavaCache == null) {
			return null;
		}
		Collection<Serializable> set = guavaCache.asMap().values();
		return set;
	}

}
