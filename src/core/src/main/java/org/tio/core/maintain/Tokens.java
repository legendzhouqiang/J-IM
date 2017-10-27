package org.tio.core.maintain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.ObjWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:40
 */
public class Tokens {
	private static Logger log = LoggerFactory.getLogger(Tokens.class);

	/**
	 * key: token
	 * value: ChannelContext
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> mapWithLock = new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());

	/**
	 * 绑定token.
	 *
	 * @param token the token
	 * @param channelContext the channel context
	 * @author tanyaowu
	 */
	public void bind(String token, ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(token)) {
			return;
		}
		String key = token;
		Lock lock = mapWithLock.getLock().writeLock();
		Map<String, SetWithLock<ChannelContext>> map = mapWithLock.getObj();

		try {
			lock.lock();

			SetWithLock<ChannelContext> setWithLock = map.get(key);
			if (setWithLock == null) {
				setWithLock = new SetWithLock<>(new HashSet<>());
				map.put(key, setWithLock);
			}
			setWithLock.add(channelContext);

			//			map.put(key, channelContext);

			channelContext.setToken(token);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Find.
	 *
	 * @param token the token
	 * @return the channel context
	 */
	public SetWithLock<ChannelContext> find(GroupContext groupContext, String token) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(token)) {
			return null;
		}
		String key = token;
		Lock lock = mapWithLock.getLock().readLock();
		Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();

		try {
			lock.lock();
			return m.get(key);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return the mapWithLock
	 */
	public ObjWithLock<Map<String, SetWithLock<ChannelContext>>> getMap() {
		return mapWithLock;
	}

	/**
	 * 解除channelContext绑定的token
	 *
	 * @param channelContext the channel context
	 */
	public void unbind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		String token = channelContext.getToken();
		if (StringUtils.isBlank(token)) {
			log.info("{}, {}, 并没有绑定用户", groupContext.getName(), channelContext.toString());
			return;
		}

		Lock lock = mapWithLock.getLock().writeLock();
		Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
		try {
			lock.lock();

			SetWithLock<ChannelContext> setWithLock = m.get(token);
			if (setWithLock == null) {
				log.info("{}, {}, token:{}, 没有找到对应的SetWithLock", groupContext.getName(), channelContext.toString(), token);
				return;
			}
			channelContext.setToken(null);
			setWithLock.remove(channelContext);
			
			
			if(setWithLock.getObj().size() == 0) {
				m.remove(token);
			}
			
			
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 解除groupContext范围内所有ChannelContext的 token绑定
	 *
	 * @param token the token
	 * @author tanyaowu
	 */
	public void unbind(GroupContext groupContext, String token) {
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(token)) {
			return;
		}

		Lock lock = mapWithLock.getLock().writeLock();
		Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
		try {
			lock.lock();

			SetWithLock<ChannelContext> setWithLock = m.get(token);
			if (setWithLock == null) {
				return;
			}

			WriteLock writeLock = setWithLock.getLock().writeLock();
			writeLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				if (set.size() > 0) {
					for (ChannelContext channelContext : set) {
						channelContext.setToken(null);
					}
					set.clear();
				}
				
				m.remove(token);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				writeLock.unlock();
			}

		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
