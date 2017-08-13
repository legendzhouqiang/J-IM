package org.tio.core.maintain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.GroupListener;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;

public class Groups {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Groups.class);

	/** 一个组有哪些客户端
	 * key: groupid
	 * value: Set<ChannelContext
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> groupmap = new MapWithLock<String, SetWithLock<ChannelContext>>(
			new ConcurrentHashMap<String, SetWithLock<ChannelContext>>());

	/** 一个客户端在哪组组中
	 *  key: ChannelContext
	 *  value: Set<groupid
	 */
	private MapWithLock<ChannelContext, SetWithLock<String>> channelmap = new MapWithLock<ChannelContext, SetWithLock<String>>(
			new ConcurrentHashMap<ChannelContext, SetWithLock<String>>());

	/**
	 * @return the groupmap
	 */
	public MapWithLock<String, SetWithLock<ChannelContext>> getGroupmap() {
		return groupmap;
	}

	/**
	 * @return the channelmap
	 */
	public MapWithLock<ChannelContext, SetWithLock<String>> getChannelmap() {
		return channelmap;
	}

	/**
	 * 与所有组解除绑定
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		Lock lock = channelmap.getLock().writeLock();
		try {
			SetWithLock<String> set = null;
			try {
				lock.lock();
				Map<ChannelContext, SetWithLock<String>> m = channelmap.getObj();
				set = m.get(channelContext);
				m.remove(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock.unlock();
			}

			if (set != null) {

				GroupListener groupListener = groupContext.getGroupListener();
				Set<String> groups = set.getObj();
				if (groups != null && groups.size() > 0) {
					for (String groupid : groups) {
						unbind(groupid, channelContext);
						if (groupListener != null) {
							try {
								groupListener.onAfterUnbind(channelContext, groupid);
							} catch (Exception e) {
								log.error(e.toString(), e);
							}
						}
						//groupListener.onAfterUnbind
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 与指定组解除绑定
	 * @param groupid
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(String groupid, ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(groupid)) {
			return;
		}

		SetWithLock<ChannelContext> set = groupmap.getObj().get(groupid);
		if (set != null) {
			Lock lock1 = set.getLock().writeLock();
			try {
				lock1.lock();
				set.getObj().remove(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock1.unlock();
			}

			if (set.getObj().size() == 0) {
				Lock lock2 = groupmap.getLock().writeLock();
				try {
					lock2.lock();
					groupmap.getObj().remove(groupid);
				} catch (Exception e) {
					log.error(e.toString(), e);
				} finally {
					lock2.unlock();
				}
			}
		}
	}

	/**
	 * 和组绑定
	 * @param groupid
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void bind(String groupid, ChannelContext channelContext) {

		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(groupid)) {
			return;
		}

		Lock lock1 = groupmap.getLock().writeLock();
		SetWithLock<ChannelContext> channelContexts = null;
		try {
			lock1.lock();
			channelContexts = groupmap.getObj().get(groupid);
			if (channelContexts == null) {
				channelContexts = new SetWithLock<ChannelContext>(new HashSet<ChannelContext>());
			}
			groupmap.getObj().put(groupid, channelContexts);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			lock1.unlock();
		}

		if (channelContexts != null) {
			Lock lock11 = channelContexts.getLock().writeLock();
			try {
				lock11.lock();
				channelContexts.getObj().add(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock11.unlock();
			}
		}

		Lock lock2 = channelmap.getLock().writeLock();
		SetWithLock<String> groups = null;// = channelmap.getObj().get(channelContext);
		try {
			lock2.lock();
			groups = channelmap.getObj().get(channelContext);
			if (groups == null) {
				groups = new SetWithLock<String>(new HashSet<String>());
			}
			channelmap.getObj().put(channelContext, groups);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			lock2.unlock();
		}

		if (groups != null) {
			Lock lock22 = groups.getLock().writeLock();
			try {
				lock22.lock();
				groups.getObj().add(groupid);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock22.unlock();
			}
		}
	}

	/**
	 * 一个组有哪些客户端
	 * @param groupid
	 * @return
	 * @author: tanyaowu
	 */
	public SetWithLock<ChannelContext> clients(GroupContext groupContext, String groupid) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(groupid)) {
			return null;
		}

		Map<String, SetWithLock<ChannelContext>> map = groupmap.getObj();
		SetWithLock<ChannelContext> set = map.get(groupid);
		return set;
	}

	/**
	 * 某个客户端在哪些组中
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public SetWithLock<String> groups(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return null;
		}

		Map<ChannelContext, SetWithLock<String>> map = channelmap.getObj();
		SetWithLock<String> set = map.get(channelContext);
		return set;
	}
}
