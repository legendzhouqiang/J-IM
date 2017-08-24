package org.tio.core.maintain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.cache.IpStatRemovalListener;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatType;
import org.tio.utils.cache.guava.GuavaCache;

/**
 *
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class Ips {
	private static Logger log = LoggerFactory.getLogger(Ips.class);

	private final static String CACHE_NAME = "TIO_IP_STAT";
	//	private final static Long timeToLiveSeconds = null;
	//	private final static Long timeToIdleSeconds = Time.DAY_1;

	private String id;

	private GuavaCache[] caches = null;
	private Map<IpStatType, GuavaCache> map = new HashMap<>();

	@SuppressWarnings("unchecked")
	public Ips(String id) {
		this.id = id;
		String cacheName = CACHE_NAME + this.id;
		IpStatType[] values = IpStatType.values();
		caches = new GuavaCache[values.length];
		int i = 0;
		for (IpStatType v : values) {
			GuavaCache guavaCache = GuavaCache.register(cacheName + v.name(), v.getTimeToLiveSeconds(), null, new IpStatRemovalListener());
			map.put(v, guavaCache);
			caches[i++] = guavaCache;
		}
	}

	/**
	 *
	 *
	 * @author: tanyaowu
	 */
	public void clear(IpStatType ipStatType) {
		GuavaCache guavaCache = map.get(ipStatType);
		guavaCache.clear();
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则创建
	 * @param ipStatType
	 * @param ip
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(IpStatType ipStatType, String ip) {
		return get(ipStatType, ip, true);
	}

	/**
	 * 根据ip获取IpStat，如果缓存中不存在，则根据forceCreate的值决定是否创建
	 * @param ipStatType
	 * @param ip
	 * @param forceCreate
	 * @return
	 * @author: tanyaowu
	 */
	public IpStat get(IpStatType ipStatType, String ip, boolean forceCreate) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		GuavaCache guavaCache = map.get(ipStatType);
		IpStat ipStat = (IpStat) guavaCache.get(ip);
		if (ipStat == null && forceCreate) {
			synchronized (this) {
				ipStat = (IpStat) guavaCache.get(ip);
				if (ipStat == null) {
					ipStat = new IpStat(ip);
					guavaCache.put(ip, ipStat);
				}
			}
		}
		return ipStat;
	}

//	public GuavaCache[] getCaches() {
//		return caches;
//	}

	//	/**
	//	 * 打印
	//	 * 
	//	 * @author: tanyaowu
	//	 */
	//	public void print() {
	//		synchronized (this) {
	//			ConcurrentMap<String, Serializable> map = caches.asMap();
	//			log.info(Json.toFormatedJson(map));
	//		}
	//	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public ConcurrentMap<String, Serializable> map(IpStatType ipStatType) {
		GuavaCache guavaCache = map.get(ipStatType);
		ConcurrentMap<String, Serializable> map = guavaCache.asMap();
		return map;
	}

	public void setCaches(GuavaCache[] caches) {
		this.caches = caches;
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public long size(IpStatType ipStatType) {
		GuavaCache guavaCache = map.get(ipStatType);
		return guavaCache.size();
	}

	/**
	 *
	 * @return
	 * @author: tanyaowu
	 */
	public Collection<Serializable> values(IpStatType ipStatType) {
		GuavaCache guavaCache = map.get(ipStatType);
		Collection<Serializable> set = guavaCache.asMap().values();
		return set;
	}

}
