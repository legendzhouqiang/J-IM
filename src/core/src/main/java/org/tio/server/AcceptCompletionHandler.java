package org.tio.server;

import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelAction;
import org.tio.core.ReadCompletionHandler;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatType;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.guava.GuavaCache;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:27:45
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {

	private static Logger log = LoggerFactory.getLogger(AioServer.class);

	public AcceptCompletionHandler() {
	}

	/**
	 *
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @author tanyaowu
	 */
	@Override
	public void completed(AsynchronousSocketChannel asynchronousSocketChannel, AioServer aioServer) {
		try {
			ServerGroupContext serverGroupContext = aioServer.getServerGroupContext();
			InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
			String clientIp = inetSocketAddress.getHostString();
//			serverGroupContext.ips.get(clientIp).getRequestCount().incrementAndGet();
			
//			GuavaCache[] caches = serverGroupContext.ips.getCaches();
//			for (GuavaCache guavaCache : caches) {
//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
//				ipStat.getRequestCount().incrementAndGet();
//			}
			
			IpStatType[] ipStatTypes = IpStatType.values();
			for (IpStatType v : ipStatTypes) {
				IpStat ipStat = (IpStat) serverGroupContext.ips.get(v, clientIp);
				ipStat.getRequestCount().incrementAndGet();
			}

			ServerGroupStat serverGroupStat = serverGroupContext.getServerGroupStat();
			serverGroupStat.getAccepted().incrementAndGet();
			
			
//			channelContext.getIpStat().getActivatedCount().incrementAndGet();
//			for (GuavaCache guavaCache : caches) {
//				IpStat ipStat = (IpStat) guavaCache.get(clientIp);
//				ipStat.getActivatedCount().incrementAndGet();
//			}
			for (IpStatType v : ipStatTypes) {
				IpStat ipStat = (IpStat) serverGroupContext.ips.get(v, clientIp);
				ipStat.getActivatedCount().incrementAndGet();
			}

			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 32 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 32 * 1024);
			asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

			ServerChannelContext channelContext = new ServerChannelContext(serverGroupContext, asynchronousSocketChannel);
			channelContext.setClosed(false);
			channelContext.setServerNode(aioServer.getServerNode());
			ServerAioListener serverAioListener = serverGroupContext.getServerAioListener();
			channelContext.getStat().setTimeFirstConnected(SystemTimer.currentTimeMillis());
			
			channelContext.traceClient(ChannelAction.CONNECT, null, null);
			
			serverGroupContext.connecteds.add(channelContext);
			try {
				serverAioListener.onAfterConnected(channelContext, true, false);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}

			if (!aioServer.isWaitingStop()) {
				ReadCompletionHandler readCompletionHandler = channelContext.getReadCompletionHandler();
				ByteBuffer readByteBuffer = readCompletionHandler.getReadByteBuffer();//ByteBuffer.allocateDirect(channelContext.getGroupContext().getReadBufferSize());
				readByteBuffer.position(0);
				readByteBuffer.limit(readByteBuffer.capacity());
				asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, readCompletionHandler);
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (aioServer.isWaitingStop()) {
				log.info("{}即将关闭服务器，不再接受新请求", aioServer.getServerNode());
			} else {
				AsynchronousServerSocketChannel serverSocketChannel = aioServer.getServerSocketChannel();
				serverSocketChannel.accept(aioServer, this);
			}
		}
	}

	/**
	 *
	 * @param exc
	 * @param aioServer
	 * @author tanyaowu
	 */
	@Override
	public void failed(Throwable exc, AioServer aioServer) {
		AsynchronousServerSocketChannel serverSocketChannel = aioServer.getServerSocketChannel();
		serverSocketChannel.accept(aioServer, this);

		log.error("[" + aioServer.getServerNode() + "]监听出现异常", exc);

	}

}
