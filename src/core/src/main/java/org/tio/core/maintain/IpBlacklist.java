package org.tio.core.maintain;

import java.util.Collection;

import org.tio.core.Aio;
import org.tio.core.GroupContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.time.Time;

/**
 *
 * @author tanyaowu
 * 2017年5月22日 下午2:53:47
 */
public class IpBlacklist {

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
//	private SetWithLock<String> setWithLock = new SetWithLock<>(new HashSet<String>());

	
	private String id;

	private final static String CACHE_NAME = "TIO_IP_BLACK_LIST";
	private final static Long TIME_TO_LIVE_SECONDS = Time.DAY_1;
	private final static Long TIME_TO_IDLE_SECONDS = null;

	private String cacheName = null;
	private CaffeineCache cache = null;
	
	private GroupContext groupContext;

	public IpBlacklist(String id, GroupContext groupContext) {
		this.id = id;
		this.groupContext = groupContext;
		this.cacheName = CACHE_NAME + this.id;
		this.cache = CaffeineCache.register(this.cacheName, TIME_TO_LIVE_SECONDS, TIME_TO_IDLE_SECONDS, null);
	}

	
	public boolean add(String ip) {
		//先添加到黑名单列表
//		Lock lock = setWithLock.getLock().writeLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			m.add(ip);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		if (isInBlacklist(ip)) {
			return false;
		}
		cache.put(ip, SystemTimer.currentTimeMillis());

		//再删除相关连接
		Aio.remove(groupContext, ip, "ip[" + ip + "]被加入了黑名单");
		return true;
	}

	public void clear() {
//		Lock lock = setWithLock.getLock().writeLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			m.clear();
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		cache.clear();
	}

	public Collection<String> getCopy() {
//		Lock lock = setWithLock.getLock().readLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			Set<String> copyObj = new HashSet<>();
//			copyObj.addAll(m);
//			return copyObj;
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		return cache.keys();
	}

	/**
	 *
	 *
	 * @return
	 */
//	public SetWithLock<String> getSetWithLock() {
//		return setWithLock;
//	}

	/**
	 * 是否在黑名单中
	 * @param ip
	 * @return
	 * @author tanyaowu
	 */
	public boolean isInBlacklist(String ip) {
//		Lock lock = setWithLock.getLock().readLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			return m.contains(ip);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		return cache.get(ip) != null;
	}

	/**
	 * 从黑名单中删除
	 * @param ip
	 * @return
	 * @author: tanyaowu
	 */
	public void remove(String ip) {
//		Lock lock = setWithLock.getLock().writeLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			return m.remove(ip);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		cache.remove(ip);
	}

//	public int size() {
//		Lock lock = setWithLock.getLock().readLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			return m.size();
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
//		
//		cache.keys()
//	}

}
