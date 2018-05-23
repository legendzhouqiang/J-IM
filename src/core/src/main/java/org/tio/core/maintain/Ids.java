package org.tio.core.maintain;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.MapWithLock;

/**
 *
 * @author tanyaowu
 * 2017年4月15日 下午12:13:19
 */
public class Ids {

	/**
	 * key: ChannelContext对象的id字段
	 * value: ChannelContext
	 */
	private MapWithLock<String, ChannelContext> map = new MapWithLock<>(new HashMap<String, ChannelContext>());

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void bind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		String key = channelContext.getId();
		if (StringUtils.isBlank(key)) {
			return;
		}
		map.put(key, channelContext);
//		Lock lock = map.writeLock();
//		lock.lock();
//		try {
//			Map<String, ChannelContext> m = map.getObj();
//			m.put(key, channelContext);
//			//			channelContext.setId(id);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
	}

	/**
	 * Find.
	 *
	 * @param id the id
	 * @return the channel context
	 */
	public ChannelContext find(GroupContext groupContext, String id) {
		if (groupContext.isShortConnection()) {
			return null;
		}

		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		return map.get(id);
//		String key = id;
//		Lock lock = map.readLock();
//		lock.lock();
//		try {
//			Map<String, ChannelContext> m = map.getObj();
//			return m.get(key);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
	}

	/**
	 * @return the cacheMap
	 */
	public MapWithLock<String, ChannelContext> getMap() {
		return map;
	}

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();
		if (groupContext.isShortConnection()) {
			return;
		}

		String key = channelContext.getId();
		if (StringUtils.isBlank(key)) {
			return;
		}
		map.remove(key);
		
//		Lock lock = map.writeLock();
//		lock.lock();
//		try {
//			Map<String, ChannelContext> m = map.getObj();
//			m.remove(key);
//		} catch (Throwable e) {
//			throw e;
//		} finally {
//			lock.unlock();
//		}
	}
}
