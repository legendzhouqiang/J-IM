package org.tio.core.maintain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 * 该维护只在Server侧有
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:27
 */
public class Ips {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Ips.class);

	/** 一个IP有哪些客户端
	 * key: ip
	 * value: SetWithLock<ChannelContext>
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> ipmap = new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());

	/**
	 * 和ip绑定
	 * @param ip
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void bind(ChannelContext channelContext) {
		if (channelContext == null) {
			return;
		}
		
		String ip = channelContext.getClientNode().getIp();
		if (ChannelContext.UNKNOWN_ADDRESS_IP.equals(ip)) {
			return;
		}
		
//		GroupContext groupContext = channelContext.getGroupContext();
//		if (groupContext.isShortConnection()) {
//			return;
//		}

		if (StringUtils.isBlank(ip)) {
			return;
		}
		
		SetWithLock<ChannelContext> channelContexts = null;//ipmap.getObj().get(ip);
		Lock lock1 = ipmap.getLock().writeLock();
		lock1.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> map = ipmap.getObj();
			channelContexts = map.get(ip);
			if (channelContexts == null) {
				channelContexts = new SetWithLock<>(new HashSet<ChannelContext>());
				map.put(ip, channelContexts);
			}
			channelContexts.add(channelContext);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			lock1.unlock();
		}

		//		if (channelContexts != null) {
//		Lock lock11 = channelContexts.getLock().writeLock();
//		lock11.lock();
//		try {
//			channelContexts.getObj().add(channelContext);
//		} catch (Throwable e) {
//			log.error(e.toString(), e);
//		} finally {
//			lock11.unlock();
//		}
		//		}

	}

	/**
	 * 一个ip有哪些客户端，有可能返回null
	 * @param ip
	 * @return
	 * @author tanyaowu
	 */
	public SetWithLock<ChannelContext> clients(GroupContext groupContext, String ip) {
//		if (groupContext.isShortConnection()) {
//			return null;
//		}

		if (StringUtils.isBlank(ip)) {
			return null;
		}

		Map<String, SetWithLock<ChannelContext>> map = ipmap.getObj();
		SetWithLock<ChannelContext> set = map.get(ip);
		return set;
	}

	/**
	 * @return the ipmap
	 */
	public MapWithLock<String, SetWithLock<ChannelContext>> getIpmap() {
		return ipmap;
	}

	/**
	 * 与指定ip解除绑定
	 * @param ip
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		if (channelContext == null) {
			return;
		}
		
		String ip = channelContext.getClientNode().getIp();
		if (ChannelContext.UNKNOWN_ADDRESS_IP.equals(ip)) {
			log.error("{} ip is not right", channelContext);
			return;
		}
		
		GroupContext groupContext = channelContext.getGroupContext();
//		if (groupContext.isShortConnection()) {
//			return;
//		}

		if (StringUtils.isBlank(ip)) {
			return;
		}

		SetWithLock<ChannelContext> channelContexts = ipmap.getObj().get(ip);
		if (channelContexts != null) {
			Lock lock1 = channelContexts.getLock().writeLock();
			lock1.lock();
			try {
				channelContexts.getObj().remove(channelContext);
				if (channelContexts.getObj().size() == 0) {
					Lock lock2 = ipmap.getLock().writeLock();
					lock2.lock();
					try {
						ipmap.getObj().remove(ip);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					} finally {
						lock2.unlock();
					}
				}
			} catch (Throwable e) {
				log.error(e.toString(), e);
			} finally {
				lock1.unlock();
			}
		} else {
			log.error("{}, ip【{}】 找不到对应的SetWithLock", groupContext.getName(), ip);
		}
	}
}
