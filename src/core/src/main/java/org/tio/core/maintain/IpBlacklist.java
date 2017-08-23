package org.tio.core.maintain;

import java.util.Collection;

import org.tio.core.Aio;
import org.tio.core.GroupContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.guava.GuavaCache;
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
	private final static Long timeToLiveSeconds = Time.DAY_1;
	private final static Long timeToIdleSeconds = null;

	private String cacheName = null;
	private GuavaCache cache = null;

	public IpBlacklist(String id) {
		this.id = id;
		this.cacheName = CACHE_NAME + this.id;
		this.cache = GuavaCache.register(this.cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
	}

	
	public void add(GroupContext groupContext, String ip) {
		//先添加到黑名单列表
//		Lock lock = setWithLock.getLock().writeLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			m.add(ip);
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
		
		cache.put(ip, SystemTimer.currentTimeMillis());

		//再删除相关连接
		Aio.remove(groupContext, ip, "ip[" + ip + "]被加入了黑名单");
	}

	public void clear() {
//		Lock lock = setWithLock.getLock().writeLock();
//		try {
//			lock.lock();
//			Set<String> m = setWithLock.getObj();
//			m.clear();
//		} catch (Exception e) {
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
//		} catch (Exception e) {
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
//		} catch (Exception e) {
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
//		} catch (Exception e) {
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
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
//		
//		cache.keys()
//	}

}
