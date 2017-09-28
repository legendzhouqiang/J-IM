package org.tio.server;

import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.stat.ChannelStat;
import org.tio.core.stat.GroupStat;
import org.tio.utils.json.Json;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.ObjWithLock;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * The Class ServerGroupContext.
 *
 * @author tanyaowu
 */
public class ServerGroupContext extends GroupContext {
	static Logger log = LoggerFactory.getLogger(ServerGroupContext.class);

	private AcceptCompletionHandler acceptCompletionHandler = null;

	private ServerAioHandler serverAioHandler = null;

	private ServerAioListener serverAioListener = null;

	protected ServerGroupStat serverGroupStat = new ServerGroupStat();

	/** The accept executor. */
	//private ThreadPoolExecutor acceptExecutor = null;

	private Thread checkHeartbeatThread = null;

	/**
	 *
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param groupExecutor
	 *
	 * @author tanyaowu
	 * 2017年2月2日 下午1:40:11
	 *
	 */
	public ServerGroupContext(ServerAioHandler serverAioHandler, ServerAioListener serverAioListener) {
		this(null, serverAioHandler, serverAioListener);
	}

	public ServerGroupContext(String name, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener) {
		this(name, serverAioHandler, serverAioListener, null, null);
	}

	public ServerGroupContext(ServerAioHandler serverAioHandler, ServerAioListener serverAioListener, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this(null, serverAioHandler, serverAioListener, tioExecutor, groupExecutor);
	}

	public ServerGroupContext(String name, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener, SynThreadPoolExecutor tioExecutor,
			ThreadPoolExecutor groupExecutor) {
		super(tioExecutor, groupExecutor);
		this.name = name;
		this.acceptCompletionHandler = new AcceptCompletionHandler();
		this.serverAioHandler = serverAioHandler;
		this.serverAioListener = serverAioListener == null ? new DefaultServerAioListener() : serverAioListener;

		checkHeartbeatThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!isStopped()) {
					//					long sleeptime = heartbeatTimeout;
					if (heartbeatTimeout <= 0) {
						log.info("{}, 用户取消了框架层面的心跳检测，如果业务需要，请用户自己去完成心跳检测", ServerGroupContext.this.name);
						break;
					}
					long start = SystemTimer.currentTimeMillis();
					ObjWithLock<Set<ChannelContext>> objWithLock = ServerGroupContext.this.connections.getSetWithLock();
					Set<ChannelContext> set = null;
					ReadLock readLock = objWithLock.getLock().readLock();
					long start1 = 0;
					int count = 0;
					try {
						readLock.lock();
						start1 = SystemTimer.currentTimeMillis();
						set = objWithLock.getObj();

						for (ChannelContext entry : set) {
							count++;
							ChannelContext channelContext = entry;
							ChannelStat stat = channelContext.getStat();
							long timeLatestReceivedMsg = stat.getLatestTimeOfReceivedByte();
							long timeLatestSentMsg = stat.getLatestTimeOfSentPacket();
							long compareTime = Math.max(timeLatestReceivedMsg, timeLatestSentMsg);
							long currtime = SystemTimer.currentTimeMillis();
							long interval = currtime - compareTime;
							if (interval > heartbeatTimeout) {
								log.info("{}, {} ms没有收发消息", channelContext, interval);
								Aio.remove(channelContext, interval + " ms没有收发消息");
							}
						}
					} catch (Throwable e) {
						log.error("", e);
					} finally {
						try {
							readLock.unlock();

							if (log.isInfoEnabled()) {
								int groups = 0;
								ObjWithLock<Set<ChannelContext>> objwithlock = ServerGroupContext.this.groups.clients(ServerGroupContext.this, "g");
								if (objwithlock != null) {
									groups = objwithlock.getObj().size();
								}

								log.info(
										"{}, [{}]:[{}]" 
												+ "\r\n当前连接个数                                 {}" 
												+ "\r\n当前共有 不同的ip数      {}"
												+ "\r\n群组(g)                 {}"
												+ "\r\n共接受连接                                      {}" 
												+ "\r\n一共关闭过的连接个数            {}"
												+ "\r\n已接收消息                                   ({}p)({}b)" 
												+ "\r\n已处理消息                                     {}p"
												+ "\r\n已发送消息                                      ({}p)({}b)" 
												+ "\r\n平均每次TCP包接收的字节数   {}" 
												+ "\r\n平均每次TCP包接收的业务包   {}"
												+ "\r\n目前IP统计的时长   {}",
										ServerGroupContext.this.name, 
										SystemTimer.currentTimeMillis(), 
										id, 
										set.size(), 
										ServerGroupContext.this.ips.getIpmap().getObj().size(),
										groups, 
										serverGroupStat.getAccepted().get(),
										serverGroupStat.getClosed().get(), 
										serverGroupStat.getReceivedPackets().get(), 
										serverGroupStat.getReceivedBytes().get(),
										serverGroupStat.getHandledPacket().get(), 
										serverGroupStat.getSentPacket().get(), 
										serverGroupStat.getSentBytes().get(),
										serverGroupStat.getBytesPerTcpReceive(), 
										serverGroupStat.getPacketsPerTcpReceive(), 
										Json.toJson(ServerGroupContext.this.ipStats.list));
							}

							//打印各集合信息
							if (log.isInfoEnabled()) {
								log.info("{}, " 
									+ "\r\nclientNodes:{}" 
									+ "\r\n所有连接:{}" 
									+ "\r\n目前连上的连接:{}" 
									+ "\r\n关闭的连接次数:{}" 
									+ "\r\n群组:[channelmap:{}, groupmap:{}]"
									+ "\r\n绑定用户数:{}" 
									+ "\r\n等待同步消息响应:{}"
								//										+ "\r\n正在被监控统计的ip数:{}"
										+ "\r\n被拉黑的ip:{}", ServerGroupContext.this.name, ServerGroupContext.this.clientNodeMap.getMap().getObj().size(),
										ServerGroupContext.this.connections.getSetWithLock().getObj().size(), ServerGroupContext.this.connecteds.getSetWithLock().getObj().size(),
										ServerGroupContext.this.closeds.getSetWithLock().getObj().size(), ServerGroupContext.this.groups.getChannelmap().getObj().size(),
										ServerGroupContext.this.groups.getGroupmap().getObj().size(), ServerGroupContext.this.users.getMap().getObj().size(),
										ServerGroupContext.this.waitingResps.getMap().getObj().size(),
										//										ServerGroupContext.this.ips.size(),
										Json.toJson(ServerGroupContext.this.ipBlacklist.getCopy()));
							}

							if (log.isInfoEnabled()) {
								long end = SystemTimer.currentTimeMillis();
								long iv1 = start1 - start;
								long iv = end - start1;
								log.info("{}, 检查心跳, 共{}个连接, 取锁耗时{}ms, 循环耗时{}ms, 心跳超时时间:{}ms", ServerGroupContext.this.name, count, iv1, iv, heartbeatTimeout);
							}
							Thread.sleep(heartbeatTimeout);
						} catch (Exception e) {
							log.error("", e);
						}
					}
				}
			}
		}, "tio-timer-checkheartbeat-" + id);
		checkHeartbeatThread.setDaemon(true);
		checkHeartbeatThread.setPriority(Thread.MIN_PRIORITY);
		checkHeartbeatThread.start();

	}

	/**
	 * @return the acceptCompletionHandler
	 */
	public AcceptCompletionHandler getAcceptCompletionHandler() {
		return acceptCompletionHandler;
	}

	/**
	 * @see org.tio.core.GroupContext#getAioHandler()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:34:37
	 *
	 */
	@Override
	public AioHandler getAioHandler() {
		return this.getServerAioHandler();
	}

	/**
	 * @see org.tio.core.GroupContext#getAioListener()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:34:37
	 *
	 */
	@Override
	public AioListener getAioListener() {
		return getServerAioListener();
	}

	/**
	 * @see org.tio.core.GroupContext#getGroupStat()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:34:37
	 *
	 */
	@Override
	public GroupStat getGroupStat() {
		return this.getServerGroupStat();
	}

	/**
	 * @return the serverAioHandler
	 */
	public ServerAioHandler getServerAioHandler() {
		return serverAioHandler;
	}

	/**
	 * @return the serverAioListener
	 */
	public ServerAioListener getServerAioListener() {
		return serverAioListener;
	}

	public ServerGroupStat getServerGroupStat() {
		return serverGroupStat;
	}

	public void setServerAioListener(ServerAioListener serverAioListener) {
		this.serverAioListener = serverAioListener;
	}

}
