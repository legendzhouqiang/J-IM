package org.tio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;

/**
 *
 * @author tanyaowu
 *
 */
public class AioServer {
	private static Logger log = LoggerFactory.getLogger(AioServer.class);

	private ServerGroupContext serverGroupContext;

	private AsynchronousServerSocketChannel serverSocketChannel;

	private Node serverNode;

	private boolean isWaitingStop = false;

	/**
	 *
	 * @param serverGroupContext
	 *
	 * @author tanyaowu
	 * 2017年1月2日 下午5:53:06
	 *
	 */
	public AioServer(ServerGroupContext serverGroupContext) {
		super();
		this.serverGroupContext = serverGroupContext;
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	/**
	 * @return the serverNode
	 */
	public Node getServerNode() {
		return serverNode;
	}

	/**
	 * @return the serverSocketChannel
	 */
	public AsynchronousServerSocketChannel getServerSocketChannel() {
		return serverSocketChannel;
	}

	/**
	 * @return the isWaitingStop
	 */
	public boolean isWaitingStop() {
		return isWaitingStop;
	}

	/**
	 * @param serverGroupContext the serverGroupContext to set
	 */
	public void setServerGroupContext(ServerGroupContext serverGroupContext) {
		this.serverGroupContext = serverGroupContext;
	}

	/**
	 * @param isWaitingStop the isWaitingStop to set
	 */
	public void setWaitingStop(boolean isWaitingStop) {
		this.isWaitingStop = isWaitingStop;
	}

	public void start(String serverIp, int serverPort) throws IOException {
		this.serverNode = new Node(serverIp, serverPort);
		//		ExecutorService groupExecutor = serverGroupContext.getGroupExecutor();

		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(serverGroupContext.getGroupExecutor());
		serverSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);

		serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);

		InetSocketAddress listenAddress = null;

		if (StringUtils.isBlank(serverIp)) {
			listenAddress = new InetSocketAddress(serverPort);
		} else {
			listenAddress = new InetSocketAddress(serverIp, serverPort);
		}

		serverSocketChannel.bind(listenAddress, 0);

		AcceptCompletionHandler acceptCompletionHandler = serverGroupContext.getAcceptCompletionHandler();
		serverSocketChannel.accept(this, acceptCompletionHandler);

		log.warn("t-io remote started, listen on {}", this.serverNode);
	}

	/**
	 * 此方法生产环境中用不到，暂未测试
	 * @return
	 *
	 * @author tanyaowu
	 * 2017年2月11日 上午8:04:04
	 *
	 */
	public boolean stop() {
		isWaitingStop = true;
		boolean ret = true;

		try {
			serverSocketChannel.close();
		} catch (IOException e1) {
			log.error(e1.toString(), e1);
		}

		ExecutorService groupExecutor = serverGroupContext.getGroupExecutor();
		ExecutorService tioExecutor = serverGroupContext.getTioExecutor();

		groupExecutor.shutdown();
		tioExecutor.shutdown();

		serverGroupContext.setStopped(true);
		try {
			ret = ret && groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
			ret = ret && tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}

		log.info(this.serverNode + " stopped");
		return ret;
	}

	//	/**
	//	 * @param serverNode the serverNode to set
	//	 */
	//	public void setServerNode(Node serverNode)
	//	{
	//		this.serverNode = serverNode;
	//	}
}
