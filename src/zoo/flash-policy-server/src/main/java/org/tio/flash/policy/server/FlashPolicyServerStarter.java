package org.tio.flash.policy.server;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
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

		serverGroupContext = new ServerGroupContext(aioHandler, aioListener, tioExecutor, groupExecutor);

		serverGroupContext.setHeartbeatTimeout(Const.TIMEOUT);

		aioServer = new AioServer(serverGroupContext);

		try {
			aioServer.start(ip, port);
		} catch (Throwable e) {
			log.error(e.toString(), e);
			System.exit(1);
		}
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

	public static void main(String[] args) throws IOException {
	}

}