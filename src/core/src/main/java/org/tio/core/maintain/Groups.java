package org.tio.core.maintain;

import java.util.Comparator;
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

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:21
 */
public class Groups {
	
	/**
	 * 对ChannelContext进行排序的比较器
	 * 该对象必须在服务启动前进行设置，并且不要再去修改，否则会导致有的排序了，有的没有排序
	 */
	private Comparator<ChannelContext> channelContextComparator = null;

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Groups.class);

	/** 一个组有哪些客户端
	 * key: groupid
	 * value: Set<ChannelContext>
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> groupmap = new MapWithLock<>(
			new ConcurrentHashMap<String, SetWithLock<ChannelContext>>());

	/** 一个客户端在哪组组中
	 *  key: ChannelContext
	 *  value: Set<groupid>
	 */
	private MapWithLock<ChannelContext, SetWithLock<String>> channelmap = new MapWithLock<>(
			new ConcurrentHashMap<ChannelContext, SetWithLock<String>>());

	/**
	 * 和组绑定
	 * @param groupid
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void bind(String groupid, ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		if (StringUtils.isBlank(groupid)) {
			return;
		}
		SetWithLock<ChannelContext> channelContexts = null;
		Lock lock1 = groupmap.getLock().writeLock();
		lock1.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> map = groupmap.getObj();
			channelContexts = map.get(groupid);
			if (channelContexts == null) {
				channelContexts = new SetWithLock<>(MaintainUtils.createSet(channelContextComparator));
				map.put(groupid, channelContexts);
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			lock1.unlock();
		}

		if (channelContexts != null) {
			Lock lock11 = channelContexts.getLock().writeLock();
			lock11.lock();
			try {
				channelContexts.getObj().add(channelContext);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			} finally {
				lock11.unlock();
			}
		}
		
		SetWithLock<String> groups = null;// = channelmap.getObj().get(channelContext);
		Lock lock2 = channelmap.getLock().writeLock();
		lock2.lock();
		try {
			groups = channelmap.getObj().get(channelContext);
			if (groups == null) {
				groups = new SetWithLock<>(new HashSet<String>());
			}
			channelmap.getObj().put(channelContext, groups);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			lock2.unlock();
		}

		if (groups != null) {
			Lock lock22 = groups.getLock().writeLock();
			lock22.lock();
			try {
				groups.getObj().add(groupid);
			} catch (Throwable e) {
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
	 * @author tanyaowu
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
	 * @return the channelmap
	 */
	public MapWithLock<ChannelContext, SetWithLock<String>> getChannelmap() {
		return channelmap;
	}

	/**
	 * @return the groupmap
	 */
	public MapWithLock<String, SetWithLock<ChannelContext>> getGroupmap() {
		return groupmap;
	}

	/**
	 * 某个客户端在哪些组中
	 * @param channelContext
	 * @return
	 * @author tanyaowu
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

	/**
	 * 与所有组解除绑定
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		try {
			SetWithLock<String> set = null;
			Lock lock = channelmap.getLock().writeLock();
			lock.lock();
			try {
				Map<ChannelContext, SetWithLock<String>> m = channelmap.getObj();
				set = m.get(channelContext);
				m.remove(channelContext);
			} catch (Throwable e) {
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
							} catch (Throwable e) {
								log.error(e.toString(), e);
							}
						}
						//groupListener.onAfterUnbind
					}
				}
			}
		} catch (Throwable e) {
			throw e;
		}
	}

	/**
	 * 与指定组解除绑定
	 * @param groupid
	 * @param channelContext
	 * @author tanyaowu
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
			lock1.lock();
			try {
				set.getObj().remove(channelContext);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			} finally {
				lock1.unlock();
			}

			if (set.getObj().size() == 0) {
				Lock lock2 = groupmap.getLock().writeLock();
				lock2.lock();
				try {
					groupmap.getObj().remove(groupid);
				} catch (Throwable e) {
					log.error(e.toString(), e);
				} finally {
					lock2.unlock();
				}
			}
		}
	}

	public Comparator<ChannelContext> getChannelContextComparator() {
		return channelContextComparator;
	}

	public void setChannelContextComparator(Comparator<ChannelContext> channelContextComparator) {
		this.channelContextComparator = channelContextComparator;
	}
}
