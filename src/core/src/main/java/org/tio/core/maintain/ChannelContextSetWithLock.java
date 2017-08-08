package org.tio.core.maintain;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.SetWithLock;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:35:09
 */
public class ChannelContextSetWithLock {

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private SetWithLock<ChannelContext> setWithLock = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());

	public void add(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		Lock lock = setWithLock.getLock().writeLock();

		try {
			lock.lock();
			Set<ChannelContext> m = setWithLock.getObj();
			m.add(channelContext);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public boolean remove(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return true;
		}

		Lock lock = setWithLock.getLock().writeLock();

		try {
			lock.lock();
			Set<ChannelContext> m = setWithLock.getObj();
			boolean ret = m.remove(channelContext);
			return ret;
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		Lock lock = setWithLock.getLock().readLock();

		try {
			lock.lock();
			Set<ChannelContext> m = setWithLock.getObj();
			return m.size();
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public SetWithLock<ChannelContext> getSetWithLock() {
		return setWithLock;
	}

}
