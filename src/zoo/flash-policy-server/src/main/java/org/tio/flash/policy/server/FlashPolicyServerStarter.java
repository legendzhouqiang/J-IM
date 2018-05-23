package org.tio.flash.policy.server;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.stat.ChannelStat;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.ObjWithLock;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 下午12:22:58
 */
public class FlashPolicyServerStarter {
	private static Logger log = LoggerFactory.getLogger(FlashPolicyServerStarter.class);

	//handler, 包括编码、解码、消息处理
	public static ServerAioHandler aioHandler = null;

	//事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
	public static ServerAioListener aioListener = null;

	//一组连接共用的上下文对象
	public static ServerGroupContext serverGroupContext = null;

	//aioServer对象
	public static AioServer aioServer = null;

	public static int count = 1;

	/**
	 * 
	 * @param ip 可以为null
	 * @param port 如果为null，则用默认的端口
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author tanyaowu
	 */
	public static void start(String ip, Integer port, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		if (port == null) {
			port = Const.PORT;
		}
		aioHandler = new FlashPolicyServerAioHandler();
		serverGroupContext = new ServerGroupContext("tio flash policy server", aioHandler, aioListener, tioExecutor, groupExecutor);
		serverGroupContext.setHeartbeatTimeout(Const.HEARTBEAT_TIMEOUT);
		aioServer = new AioServer(serverGroupContext);

		try {
			aioServer.start(ip, port);
		} catch (Throwable e) {
			log.error(e.toString(), e);
			System.exit(1);
		}

		checkAllChannels();
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 * @author tanyaowu
	 */
	public static void start(String ip, Integer port) {
		start(ip, port, Threads.tioExecutor, Threads.groupExecutor);
	}

	/**
	 * 检查所有通道
	 */
	private static void checkAllChannels() {
		Thread thread = new Thread(new CheckRunnable(), "Flash-Policy-Server-" + count++);
		thread.start();

	}

	public static void main(String[] args) throws IOException {
	}

	public static class CheckRunnable implements Runnable {
		@Override
		public void run() {

			while (true) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e1) {
					log.error(e1.toString(), e1);
				}

				ObjWithLock<Set<ChannelContext>> objWithLock = serverGroupContext.connecteds;
				Set<ChannelContext> set = null;
				ReadLock readLock = objWithLock.readLock();
				readLock.lock();
				try {
					long now = SystemTimer.currentTimeMillis();
					set = objWithLock.getObj();
					for (ChannelContext channelContext : set) {
						ChannelStat channelStat = channelContext.stat;
						Long timeFirstConnected = channelStat.getTimeFirstConnected();
						long interval = (now - timeFirstConnected);
						if (interval > 5000) {
							Aio.remove(channelContext, "已经连上来有" + interval + "ms了，该断开啦");
						}
					}
				} catch (java.lang.Throwable e) {
					log.error("", e);
				} finally {
					readLock.unlock();
				}
			}
		}
	}
}