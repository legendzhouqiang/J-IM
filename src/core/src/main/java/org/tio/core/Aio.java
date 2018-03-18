package org.tio.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.core.task.SendRunnable;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.ObjWithLock;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.page.Page;
import org.tio.utils.page.PageUtils;
import org.tio.utils.thread.ThreadUtils;

/**
 * The Class Aio. t-io用户关心的API几乎全在这
 *
 * @author tanyaowu
 */
public class Aio {
	private Aio() {}


	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Aio.class);

	/**
	 * 绑定群组
	 * @param channelContext
	 * @param group
	 * @author tanyaowu
	 */
	public static void bindGroup(ChannelContext channelContext, String group) {
		channelContext.getGroupContext().groups.bind(group, channelContext);
	}
	
	/**
	 * 某通道是否在某群组中
	 * @param channelContext
	 * @param group
	 * @return true：在该群组
	 * @author: tanyaowu
	 */
	public static boolean isInGroup(ChannelContext channelContext, String group) {
		MapWithLock<ChannelContext, SetWithLock<String>> mapWithLock = 
				channelContext.getGroupContext().groups.getChannelmap();
		
		ReadLock lock = mapWithLock.getLock().readLock();
		try {
			lock.lock();
			Map<ChannelContext, SetWithLock<String>> m = mapWithLock.getObj();
			if (m == null || m.size() == 0) {
				return false;
			}
			SetWithLock<String> set = m.get(channelContext);
			if (set == null) {
				return false;
			}
			return set.getObj().contains(group);
		} catch (Throwable e) {
			log.error(e.toString(), e);
			return false;
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 群组有多少个连接
	 * @param groupContext
	 * @param group
	 * @return
	 */
	public static int groupCount(GroupContext groupContext, String group) {
		SetWithLock<ChannelContext> setWithLock = groupContext.groups.clients(groupContext, group);
		if (setWithLock == null) {
			return 0;
		}
		
		return setWithLock.getObj().size();
	}

	/**
	 * 绑定用户
	 * @param channelContext
	 * @param userid
	 * @author tanyaowu
	 */
	public static void bindUser(ChannelContext channelContext, String userid) {
		channelContext.getGroupContext().users.bind(userid, channelContext);
	}
	
	/**
	 * 绑定token
	 * @param channelContext
	 * @param token
	 * @author tanyaowu
	 */
	public static void bindToken(ChannelContext channelContext, String token) {
		channelContext.getGroupContext().tokens.bind(token, channelContext);
	}

	/**
	 * 阻塞发送消息到指定ChannelContext
	 * @param channelContext
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSend(ChannelContext channelContext, Packet packet) {
		if (channelContext == null) {
			return false;
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		return send(channelContext, packet, countDownLatch, PacketSendMode.SINGLE_BLOCK);
	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSend(GroupContext groupContext, String ip, int port, Packet packet) {
		return send(groupContext, ip, port, packet, true);
	}

	/**
	 * 发消息到所有连接
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToAll(groupContext, packet, channelContextFilter, true);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static void bSendToGroup(GroupContext groupContext, String group, Packet packet) {
		bSendToGroup(groupContext, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToGroup(groupContext, group, packet, channelContextFilter, true);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelId
	 * @param packet
	 * @author tanyaowu
	 */
	public static void bSendToId(GroupContext groupContext, String channelId, Packet packet) {
		sendToId(groupContext, channelId, packet, true);
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToSet(GroupContext groupContext, ObjWithLock<Set<ChannelContext>> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToSet(groupContext, setWithLock, packet, channelContextFilter, true);
	}

	/**
	 * 阻塞发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToUser(GroupContext groupContext, String userid, Packet packet) {
		return sendToUser(groupContext, userid, packet, true);
	}

	/**
	 * 阻塞发消息到指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToToken(GroupContext groupContext, String token, Packet packet) {
		return sendToToken(groupContext, token, packet, true);
	}
	/**
	 * 关闭连接
	 * @param channelContext
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(ChannelContext channelContext, String remark) {
		close(channelContext, null, remark);
	}

	/**
	 * 关闭连接
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(ChannelContext channelContext, Throwable throwable, String remark) {
		close(channelContext, throwable, remark, false);
	}

	/**
	 *
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isNeedRemove
	 * @author tanyaowu
	 */
	private static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
		if (channelContext.isWaitingClose()) {
			log.debug("{} 正在等待被关闭", channelContext);
			return;
		}

		synchronized (channelContext) {
			//double check
			if (channelContext.isWaitingClose()) {
				log.debug("{} 正在等待被关闭", channelContext);
				return;
			}
			channelContext.setWaitingClose(true);
			ThreadPoolExecutor closePoolExecutor = channelContext.getGroupContext().getTioExecutor();
			closePoolExecutor.execute(new CloseRunnable(channelContext, throwable, remark, isNeedRemove));
		}
	}

	/**
	 * 关闭连接
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		ChannelContext channelContext = groupContext.clientNodeMap.find(clientIp, clientPort);
		close(channelContext, throwable, remark);
	}

	/**
	 * 获取所有连接，包括当前处于断开状态的
	 * @param groupContext
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getAllChannelContexts(GroupContext groupContext) {
		return groupContext.connections;
	}

	/**
	 * 获取所有处于正常连接状态的连接
	 * @param groupContext
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getAllConnectedsChannelContexts(GroupContext groupContext) {
		return groupContext.connecteds;
	}

	/**
	 * 根据clientip和clientport获取ChannelContext
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getChannelContextByClientNode(GroupContext groupContext, String clientIp, Integer clientPort) {
		return groupContext.clientNodeMap.find(clientIp, clientPort);
	}

	/**
	 * 根据id获取ChannelContext
	 * @param channelId
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getChannelContextById(GroupContext groupContext, String channelId) {
		return groupContext.ids.find(groupContext, channelId);
	}

	/**
	 * 根据userid获取SetWithLock<ChannelContext>
	 * @param groupContext
	 * @param userid
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByUserid(GroupContext groupContext, String userid) {
		return groupContext.users.find(groupContext, userid);
	}
	
	/**
	 * 根据token获取SetWithLock<ChannelContext>
	 * @param groupContext
	 * @param token
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByToken(GroupContext groupContext, String token) {
		return groupContext.tokens.find(groupContext, token);
	}

	/**
	 * 获取一个组的所有客户端
	 * @param groupContext
	 * @param group
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByGroup(GroupContext groupContext, String group) {
		return groupContext.groups.clients(groupContext, group);
	}

	/**
	 *
	 * @param groupContext
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize) {
		SetWithLock<ChannelContext> setWithLock = Aio.getAllChannelContexts(groupContext);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize);
	}

	/**
	 * 这个方法是给服务器端用的
	 * @param groupContext
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfConnecteds(GroupContext groupContext, Integer pageIndex, Integer pageSize) {
		ObjWithLock<Set<ChannelContext>> objWithLock = Aio.getAllConnectedsChannelContexts(groupContext);
		return PageUtils.fromSetWithLock(objWithLock, pageIndex, pageSize);
	}

	/**
	 *
	 * @param groupContext
	 * @param group
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize) {
		ObjWithLock<Set<ChannelContext>> objWithLock = Aio.getChannelContextsByGroup(groupContext, group);
		return PageUtils.fromSetWithLock(objWithLock, pageIndex, pageSize);
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(ChannelContext channelContext, String remark) {
		remove(channelContext, null, remark);
	}

	/**
	 * 删除client ip为指定值的所有连接
	 * @param groupContext
	 * @param ip
	 * @param remark
	 * @author: tanyaowu
	 */
	public static void remove(GroupContext groupContext, String ip, String remark) {
		SetWithLock<ChannelContext> setWithLock = Aio.getAllChannelContexts(groupContext);
		Lock lock2 = setWithLock.getLock().readLock();
		try {
			lock2.lock();
			Set<ChannelContext> set = setWithLock.getObj();
			for (ChannelContext channelContext : set) {
				String clientIp = channelContext.getClientNode().getIp();
				if (StringUtils.equals(clientIp, ip)) {
					Aio.remove(channelContext, remark);
				}
			}
		} finally {
			lock2.unlock();
		}
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(ChannelContext channelContext, Throwable throwable, String remark) {
		close(channelContext, throwable, remark, true);
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		ChannelContext channelContext = groupContext.clientNodeMap.find(clientIp, clientPort);
		remove(channelContext, throwable, remark);
	}

	/**
	 * 发送消息到指定ChannelContext
	 * @param channelContext
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean send(ChannelContext channelContext, Packet packet) {
		return send(channelContext, packet, null, null);
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param countDownLatch
	 * @param packetSendMode
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean send(final ChannelContext channelContext, final Packet packet, CountDownLatch countDownLatch, PacketSendMode packetSendMode) {
		try {
			if (packet == null) {
				return false;
			}

			if (channelContext == null || channelContext.isClosed() || channelContext.isRemoved()) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				if (channelContext != null) {
					log.error("can send data, {}, isClosed:{}, isRemoved:{}, stack:{} ", channelContext, channelContext.isClosed(), channelContext.isRemoved(), ThreadUtils.stackTrace());
				}
				return false;
			}

			boolean isSingleBlock = countDownLatch != null && packetSendMode == PacketSendMode.SINGLE_BLOCK;

			SendRunnable sendRunnable = channelContext.getSendRunnable();
			boolean isAdded = false;
			if (countDownLatch == null) {
				isAdded = sendRunnable.addMsg(packet);
			} else {
				Meta meta = new Meta();
				meta.setCountDownLatch(countDownLatch);
				packet.setMeta(meta);
				isAdded = sendRunnable.addMsg(packet);
			}

			if (!isAdded) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				return false;
			}

			//SynThreadPoolExecutor synThreadPoolExecutor = channelContext.getGroupContext().getGroupExecutor();
			channelContext.getGroupContext().getTioExecutor().execute(sendRunnable);

			if (isSingleBlock) {
				long timeout = 10;
				try {
					channelContext.traceBlockPacket(SynPacketAction.BEFORE_WAIT, packet, countDownLatch, null);
					Boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
					channelContext.traceBlockPacket(SynPacketAction.AFTER__WAIT, packet, countDownLatch, null);
					//log.error("{} after await, packet:{}, countDownLatch:{}", channelContext, packet.logstr(), countDownLatch);

					if (!awaitFlag) {
						log.error("{}, 阻塞发送超时, timeout:{}s, packet:{}", channelContext, timeout, packet.logstr());
					}
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}

				Boolean isSentSuccess = packet.getMeta().getIsSentSuccess();
				return isSentSuccess;
			} else {
				return true;
			}
		} catch (Throwable e) {
			log.error(channelContext + ", " + e.toString(), e);
			return false;
		} finally {
			//			if (isSingleBlock)
			//			{
			//				org.tio.core.GroupContext.SYN_SEND_SEMAPHORE.release();
			//			}
		}

	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean send(GroupContext groupContext, String ip, int port, Packet packet) {
		return send(groupContext, ip, port, packet, false);
	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean send(GroupContext groupContext, String ip, int port, Packet packet, boolean isBlock) {
		ChannelContext channelContext = groupContext.clientNodeMap.find(ip, port);
		if (channelContext != null) {
			if (isBlock) {
				return bSend(channelContext, packet);
			} else {
				return send(channelContext, packet);
			}
		} else {
			log.info("{}, can find channelContext by {}:{}", groupContext.getName(), ip, port);
			return false;
		}
	}

	public static void sendToAll(GroupContext groupContext, Packet packet) {
		sendToAll(groupContext, packet, null);
	}

	/**
	 * 发消息到所有连接
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToAll(groupContext, packet, channelContextFilter, false);
	}

	/**
	 *
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		ObjWithLock<Set<ChannelContext>> setWithLock = groupContext.connections;
		if (setWithLock == null) {
			log.debug("{}, 没有任何连接", groupContext.getName());
			return false;
		}

		return sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static void sendToGroup(GroupContext groupContext, String group, Packet packet) {
		sendToGroup(groupContext, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToGroup(groupContext, group, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	private static Boolean sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		ObjWithLock<Set<ChannelContext>> setWithLock = groupContext.groups.clients(groupContext, group);
		if (setWithLock == null) {
			log.debug("{}, 组[{}]不存在", groupContext.getName(), group);
			return false;
		}
		return sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
	}
	
	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static void bSendToIp(GroupContext groupContext, String ip, Packet packet) {
		bSendToIp(groupContext, ip, packet, null);
	}

	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @return
	 * @author: tanyaowu
	 */
	public static Boolean bSendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToIp(groupContext, ip, packet, channelContextFilter, true);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static void sendToIp(GroupContext groupContext, String ip, Packet packet) {
		sendToIp(groupContext, ip, packet, null);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @author: tanyaowu
	 */
	public static void sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToIp(groupContext, ip, packet, channelContextFilter, false);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @return
	 * @author: tanyaowu
	 */
	private static Boolean sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		ObjWithLock<Set<ChannelContext>> setWithLock = groupContext.ips.clients(groupContext, ip);
		if (setWithLock == null) {
			log.info("{}, 没有ip为[{}]的对端", groupContext.getName(), ip);
			return false;
		}

		return sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean sendToId(GroupContext groupContext, String channelId, Packet packet) {
		return sendToId(groupContext, channelId, packet, false);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelId
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean sendToId(GroupContext groupContext, String channelId, Packet packet, boolean isBlock) {
		ChannelContext channelContext = Aio.getChannelContextById(groupContext, channelId);
		if (channelContext == null) {
			return false;
		}
		if (isBlock) {
			return bSend(channelContext, packet);
		} else {
			return send(channelContext, packet);
		}
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToSet(GroupContext groupContext, ObjWithLock<Set<ChannelContext>> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToSet(groupContext, setWithLock, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToSet(GroupContext groupContext, 
			ObjWithLock<Set<ChannelContext>> setWithLock, 
			Packet packet, ChannelContextFilter channelContextFilter,
			boolean isBlock) {

		Lock lock = setWithLock.getLock().readLock();
		boolean releasedLock = false;
		try {
			lock.lock();
			Set<ChannelContext> set = setWithLock.getObj();
			if (set.size() == 0) {
				log.debug("{}, 集合为空", groupContext.getName());
				return false;
			}
			CountDownLatch countDownLatch = null;
			if (isBlock) {
				countDownLatch = new CountDownLatch(set.size());
			}
			int sendCount = 0;
			for (ChannelContext channelContext : set) {
				if (channelContextFilter != null) {
					boolean isfilter = channelContextFilter.filter(channelContext);
					if (!isfilter) {
						if (isBlock) {
							countDownLatch.countDown();
						}
						continue;
					}
				}

				sendCount++;
				if (isBlock) {
					channelContext.traceBlockPacket(SynPacketAction.BEFORE_WAIT, packet, countDownLatch, null);
					send(channelContext, packet, countDownLatch, PacketSendMode.GROUP_BLOCK);
				} else {
					send(channelContext, packet, null, null);
				}
			}
			lock.unlock();
			releasedLock = true;

			if (sendCount == 0) {
				return false;
			}

			if (isBlock) {
				try {
					long timeout = sendCount / 5;
					timeout = timeout < 10 ? 10 : timeout;
					boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
					if (!awaitFlag) {
						log.error("{}, 同步群发超时, size:{}, timeout:{}, packet:{}", groupContext.getName(), setWithLock.getObj().size(), timeout, packet.logstr());
						return false;
					} else {
						return true;
					}
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
					return false;
				} finally {

				}
			} else {
				return true;
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
			return false;
		} finally {
			//			if (isBlock)
			//			{
			//				org.tio.core.GroupContext.SYN_SEND_SEMAPHORE.release();
			//			}
			if (!releasedLock) {
				lock.unlock();
			}
		}
	}

	/**
	 * 发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet) {
		return sendToUser(groupContext, userid, packet, false);
	}
	
	/**
	 * 发消息到指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean sendToToken(GroupContext groupContext, String token, Packet packet) {
		return sendToToken(groupContext, token, packet, false);
	}

	/**
	 * 发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = groupContext.users.find(groupContext, userid);
		try {
			if (setWithLock == null) {
				return false;
			}
			
			ReadLock readLock = setWithLock.getLock().readLock();
			readLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				boolean ret = false;
				for (ChannelContext channelContext : set) {
					boolean singleRet = false;
					// 不要用 a = a || b()，容易漏执行后面的函数
					if (isBlock) {
						singleRet = bSend(channelContext, packet);
					} else {
						singleRet = send(channelContext, packet);
					}
					if (singleRet) {
						ret = true;
					}
				}
				return ret;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				readLock.unlock();
			}
			return false;
		} finally {
			
		}
	}
	
	/**
	 * 发消息给指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToToken(GroupContext groupContext, String token, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = groupContext.tokens.find(groupContext, token);
		try {
			if (setWithLock == null) {
				return false;
			}
			
			ReadLock readLock = setWithLock.getLock().readLock();
			readLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				boolean ret = false;
				for (ChannelContext channelContext : set) {
					boolean singleRet = false;
					// 不要用 a = a || b()，容易漏执行后面的函数
					if (isBlock) {
						singleRet = bSend(channelContext, packet);
					} else {
						singleRet = send(channelContext, packet);
					}
					if (singleRet) {
						ret = true;
					}
				}
				return ret;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				readLock.unlock();
			}
			return false;
		} finally {
			
		}
	}

	/**
	 * 发送并等待响应.<br>
	 * 注意：<br>
	 * 1、参数packet的synSeq不为空且大于0（null、等于小于0都不行）<br>
	 * 2、对端收到此消息后，需要回一条synSeq一样的消息<br>
	 * 3、对于同步发送，框架层面并不会帮应用去调用handler.handler(packet, channelContext)方法，应用需要自己去处理响应的消息包，参考：groupContext.getAioHandler().handler(packet, channelContext);<br>
	 *
	 * @param channelContext
	 * @param packet
	 * @param timeout
	 * @return
	 * @author tanyaowu
	 */
	@SuppressWarnings("finally")
	public static Packet synSend(ChannelContext channelContext, Packet packet, long timeout) {
		Integer synSeq = packet.getSynSeq();
		if (synSeq == null || synSeq <= 0) {
			throw new RuntimeException("synSeq必须大于0");
		}

		MapWithLock<Integer, Packet> waitingResps = channelContext.getGroupContext().getWaitingResps();
		try {
			waitingResps.put(synSeq, packet);

			synchronized (packet) {
				send(channelContext, packet);
				try {
					packet.wait(timeout);
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			Packet respPacket = waitingResps.remove(synSeq);
			if (respPacket == null) {
				log.error("respPacket == null,{}", channelContext);
				return null;
			}
			if (respPacket == packet) {
				log.error("{}, 同步发送超时, {}", channelContext.getGroupContext().getName(), channelContext);
				return null;
			}
			return respPacket;
		}
	}

	/**
	 * 与所有组解除解绑关系
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(ChannelContext channelContext) {
		channelContext.getGroupContext().groups.unbind(channelContext);
	}

	/**
	 * 与指定组解除绑定关系
	 * @param group
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(String group, ChannelContext channelContext) {
		channelContext.getGroupContext().groups.unbind(group, channelContext);
	}

	//	org.tio.core.GroupContext.ipBlacklist

	/**
	 * 解除channelContext绑定的userid
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindUser(ChannelContext channelContext) {
		channelContext.getGroupContext().users.unbind(channelContext);
	}
	
	/**
	 * 解除channelContext绑定的token
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindToken(ChannelContext channelContext) {
		channelContext.getGroupContext().tokens.unbind(channelContext);
	}

	/**
	 * 解除userid的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param groupContext
	 * @param userid
	 * @author: tanyaowu
	 */
	public static void unbindUser(GroupContext groupContext, String userid) {
		groupContext.users.unbind(groupContext, userid);
	}

}
