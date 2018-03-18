package org.tio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.stat.ChannelStat;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:29:58
 */
public class AioClient {
	/**
	 * 自动重连任务
	 * @author tanyw
	 *
	 */
	private static class ReconnRunnable implements Runnable {
		ClientChannelContext channelContext = null;
		AioClient aioClient = null;

		//		private static Map<Node, Long> cacheMap = new HashMap<>();

		public ReconnRunnable(ClientChannelContext channelContext, AioClient aioClient) {
			this.channelContext = channelContext;
			this.aioClient = aioClient;
		}

		/**
		 * @see java.lang.Runnable#run()
		 *
		 * @author tanyaowu
		 * 2017年2月2日 下午8:24:40
		 *
		 */
		@Override
		public void run() {
			ReentrantReadWriteLock closeLock = channelContext.getCloseLock();
			WriteLock writeLock = closeLock.writeLock();

			try {
				writeLock.lock();
				if (!channelContext.isClosed()) //已经连上了，不需要再重连了
				{
					return;
				}
				long start = SystemTimer.currentTimeMillis();
				aioClient.reconnect(channelContext, 2);
				long end = SystemTimer.currentTimeMillis();
				long iv = end - start;
				if (iv >= 100) {
					log.error("{},重连耗时:{} ms", channelContext, iv);
				} else {
					log.info("{},重连耗时:{} ms", channelContext, iv);
				}

				if (channelContext.isClosed()) {
					channelContext.setReconnCount(channelContext.getReconnCount() + 1);
					//					cacheMap.put(channelContext.getServerNode(), SystemTimer.currentTimeMillis());
					return;
				}
			} catch (java.lang.Throwable e) {
				log.error(e.toString(), e);
			} finally {
				writeLock.unlock();
			}

		}
	}

	private static Logger log = LoggerFactory.getLogger(AioClient.class);

	private AsynchronousChannelGroup channelGroup;

	private ClientGroupContext clientGroupContext;

	/**
	 * @param serverIp 可以为空
	 * @param serverPort
	 * @param aioDecoder
	 * @param aioEncoder
	 * @param aioHandler
	 *
	 * @author tanyaowu
	 * @throws IOException
	 *
	 */
	public AioClient(final ClientGroupContext clientGroupContext) throws IOException {
		super();
		this.clientGroupContext = clientGroupContext;
		ThreadPoolExecutor groupExecutor = clientGroupContext.getGroupExecutor();
		this.channelGroup = AsynchronousChannelGroup.withThreadPool(groupExecutor);

		startHeartbeatTask();
		startReconnTask();
	}

	/**
	 *
	 * @param serverNode
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode) throws Exception {
		asynConnect(serverNode, null);
	}

	/**
	 *
	 * @param serverNode
	 * @param timeout
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, Integer timeout) throws Exception {
		asynConnect(serverNode, null, null, timeout);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param timeout
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
		connect(serverNode, bindIp, bindPort, null, timeout, false);
	}

	/**
	 *
	 * @param serverNode
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public ClientChannelContext connect(Node serverNode) throws Exception {
		return connect(serverNode, null);
	}

	/**
	 *
	 * @param serverNode
	 * @param timeout
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public ClientChannelContext connect(Node serverNode, Integer timeout) throws Exception {
		return connect(serverNode, null, 0, timeout);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param initClientChannelContext
	 * @param timeout 超时时间，单位秒
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout) throws Exception {
		return connect(serverNode, bindIp, bindPort, initClientChannelContext, timeout, true);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param initClientChannelContext
	 * @param timeout 超时时间，单位秒
	 * @param isSyn true: 同步, false: 异步
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	private ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout, boolean isSyn)
			throws Exception {

		AsynchronousSocketChannel asynchronousSocketChannel = null;
		ClientChannelContext channelContext = null;
		boolean isReconnect = initClientChannelContext != null;
		//		ClientAioListener clientAioListener = clientGroupContext.getClientAioListener();

		long start = SystemTimer.currentTimeMillis();
		asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		if (iv >= 100) {
			log.error("{}, open 耗时:{} ms", channelContext, iv);
		}

		asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		InetSocketAddress bind = null;
		if (bindPort != null && bindPort > 0) {
			if (StringUtils.isNotBlank(bindIp)) {
				bind = new InetSocketAddress(bindIp, bindPort);
			} else {
				bind = new InetSocketAddress(bindPort);
			}
		}

		if (bind != null) {
			asynchronousSocketChannel.bind(bind);
		}

		channelContext = initClientChannelContext;

		start = SystemTimer.currentTimeMillis();

		InetSocketAddress inetSocketAddress = new InetSocketAddress(serverNode.getIp(), serverNode.getPort());

		ConnectionCompletionVo attachment = new ConnectionCompletionVo(channelContext, this, isReconnect, asynchronousSocketChannel, serverNode, bindIp, bindPort);

		if (isSyn) {
			Integer realTimeout = timeout;
			if (realTimeout == null) {
				realTimeout = 5;
			}

			CountDownLatch countDownLatch = new CountDownLatch(1);
			attachment.setCountDownLatch(countDownLatch);
			asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientGroupContext.getConnectionCompletionHandler());
			countDownLatch.await(realTimeout, TimeUnit.SECONDS);
			return attachment.getChannelContext();
		} else {
			asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientGroupContext.getConnectionCompletionHandler());
			return null;
		}
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param timeout 超时时间，单位秒
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
		return connect(serverNode, bindIp, bindPort, null, timeout);
	}

	/**
	 * @return the channelGroup
	 */
	public AsynchronousChannelGroup getChannelGroup() {
		return channelGroup;
	}

	/**
	 * @return the clientGroupContext
	 */
	public ClientGroupContext getClientGroupContext() {
		return clientGroupContext;
	}

	/**
	 *
	 * @param channelContext
	 * @param timeout
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void reconnect(ClientChannelContext channelContext, Integer timeout) throws Exception {
		connect(channelContext.getServerNode(), channelContext.getBindIp(), channelContext.getBindPort(), channelContext, timeout);
	}

	/**
	 * @param clientGroupContext the clientGroupContext to set
	 */
	public void setClientGroupContext(ClientGroupContext clientGroupContext) {
		this.clientGroupContext = clientGroupContext;
	}

	/**
	 * 定时任务：发心跳
	 * @author tanyaowu
	 *
	 */
	private void startHeartbeatTask() {
		final ClientGroupStat clientGroupStat = clientGroupContext.getClientGroupStat();
		final ClientAioHandler aioHandler = clientGroupContext.getClientAioHandler();

		final String id = clientGroupContext.getId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!clientGroupContext.isStopped()) {
					final long heartbeatTimeout = clientGroupContext.getHeartbeatTimeout();
					if (heartbeatTimeout <= 0) {
						log.warn("用户取消了框架层面的心跳定时发送功能，请用户自己去完成心跳机制");
						break;
					}
					ReadLock readLock = null;
					try {
						SetWithLock<ChannelContext> setWithLock = clientGroupContext.connecteds;
						readLock = setWithLock.getLock().readLock();
						readLock.lock();
						Set<ChannelContext> set = setWithLock.getObj();
						long currtime = SystemTimer.currentTimeMillis();
						for (ChannelContext entry : set) {
							ClientChannelContext channelContext = (ClientChannelContext) entry;
							if (channelContext.isClosed() || channelContext.isRemoved()) {
								continue;
							}

							ChannelStat stat = channelContext.getStat();
							long latestTimeOfReceivedByte = stat.getLatestTimeOfReceivedByte();
							long latestTimeOfSentPacket = stat.getLatestTimeOfSentPacket();
							long compareTime = Math.max(latestTimeOfReceivedByte, latestTimeOfSentPacket);
							long interval = currtime - compareTime;
							if (interval >= heartbeatTimeout / 2) {
								Packet packet = aioHandler.heartbeatPacket();
								if (packet != null) {
									log.info("{}发送心跳包", channelContext.toString());
									Aio.send(channelContext, packet);
								}
							}
						}
						if (log.isInfoEnabled()) {
							log.info("[{}]: curr:{}, closed:{}, received:({}p)({}b), handled:{}, sent:({}p)({}b)", id, set.size(), clientGroupStat.getClosed().get(),
									clientGroupStat.getReceivedPackets().get(), clientGroupStat.getReceivedBytes().get(), clientGroupStat.getHandledPacket().get(),
									clientGroupStat.getSentPackets().get(), clientGroupStat.getSentBytes().get());
						}

					} catch (Throwable e) {
						log.error("", e);
					} finally {
						try {
							if (readLock != null) {
								readLock.unlock();
							}
							Thread.sleep(heartbeatTimeout / 4);
						} catch (Throwable e) {
							log.error(e.toString(), e);
						} finally {

						}
					}
				}
			}
		}, "tio-timer-heartbeat" + id).start();
	}

	/**
	 * 启动重连任务
	 *
	 *
	 * @author tanyaowu
	 *
	 */
	private void startReconnTask() {
		final ReconnConf reconnConf = clientGroupContext.getReconnConf();
		if (reconnConf == null || reconnConf.getInterval() <= 0) {
			return;
		}

		final String id = clientGroupContext.getId();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!clientGroupContext.isStopped()) {
					//log.info("准备重连");
					LinkedBlockingQueue<ChannelContext> queue = reconnConf.getQueue();
					ClientChannelContext channelContext = null;
					try {
						channelContext = (ClientChannelContext) queue.take();
					} catch (InterruptedException e1) {
						log.error(e1.toString(), e1);
					}
					if (channelContext == null) {
						continue;
						//						return;
					}

					if (channelContext.isRemoved()) //已经删除的，不需要重新再连
					{
						continue;
					}
					
					SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
					if (sslFacadeContext != null) {
						sslFacadeContext.setHandshakeCompleted(false);
					}

					long currtime = SystemTimer.currentTimeMillis();
					long timeInReconnQueue = channelContext.getStat().getTimeInReconnQueue();
					long sleeptime = reconnConf.getInterval() - (currtime - timeInReconnQueue);
					//log.info("sleeptime:{}, closetime:{}", sleeptime, timeInReconnQueue);
					if (sleeptime > 0) {
						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
					}

					if (channelContext.isRemoved() || !channelContext.isClosed()) //已经删除的和已经连上的，不需要重新再连
					{
						continue;
					}
					ReconnRunnable runnable = new ReconnRunnable(channelContext, AioClient.this);
					reconnConf.getThreadPoolExecutor().execute(runnable);
				}
			}
		});
		thread.setName("tio-timer-reconnect-" + id);
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * 此方法生产环境中用不到，暂未测试
	 * @return
	 *
	 * @author tanyaowu
	 *
	 */
	public boolean stop() {
		//		isWaitingStop = true;
		boolean ret = true;
		ExecutorService groupExecutor = clientGroupContext.getGroupExecutor();
		SynThreadPoolExecutor tioExecutor = clientGroupContext.getTioExecutor();
		groupExecutor.shutdown();
		tioExecutor.shutdown();
		clientGroupContext.setStopped(true);
		try {
			ret = ret && groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
			ret = ret && tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		log.info("client resource has released");
		return ret;

	}
}
