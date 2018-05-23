package org.tio.core.maintain;

import java.util.concurrent.locks.Lock;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.utils.lock.ObjWithLock;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:35:20
 */
public class ClientNodeMap {

	/**
	 *
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static String getKey(ChannelContext channelContext) {
		Node clientNode = channelContext.getClientNode();
		if (clientNode == null) {
			throw new RuntimeException("client node is null");
		}
		String key = getKey(clientNode.getIp(), clientNode.getPort());
		return key;
	}

	/**
	 *
	 * @param ip
	 * @param port
	 * @return
	 * @author tanyaowu
	 */
	public static String getKey(String ip, int port) {
		String key = ip + ":" + port;
		return key;
	}

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private ObjWithLock<DualHashBidiMap<String, ChannelContext>> map = new ObjWithLock<>(new DualHashBidiMap<String, ChannelContext>());

	/**
	 *
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	public ChannelContext find(String key) {
		Lock lock = map.readLock();
		lock.lock();
		try {
			DualHashBidiMap<String, ChannelContext> m = map.getObj();
			return m.get(key);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 *
	 * @param ip
	 * @param port
	 * @return
	 * @author tanyaowu
	 */
	public ChannelContext find(String ip, int port) {
		String key = getKey(ip, port);
		return find(key);
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public ObjWithLock<DualHashBidiMap<String, ChannelContext>> getMap() {
		return map;
	}

	/**
	 * 添加映射
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void put(ChannelContext channelContext) {
		Lock lock = map.writeLock();
		lock.lock();
		try {
			String key = getKey(channelContext);
			DualHashBidiMap<String, ChannelContext> m = map.getObj();
			m.put(key, channelContext);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Removes映射
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void remove(ChannelContext channelContext) {
		Lock lock = map.writeLock();
		lock.lock();
		try {
			DualHashBidiMap<String, ChannelContext> m = map.getObj();
			m.removeValue(channelContext);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

}
