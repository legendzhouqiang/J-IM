package org.tio.http.server;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpSessionContext;
import org.tio.http.common.http.HttpConst;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpServerAioListener implements ServerAioListener<HttpSessionContext, HttpPacket, Object> {

	private static Logger log = LoggerFactory.getLogger(HttpServerAioListener.class);
	private static Logger iplog = LoggerFactory.getLogger("tio-ip-trace-log");

	static Map<String, AtomicLong> ipmap = new java.util.concurrent.ConcurrentHashMap<>();
	static AtomicLong accessCount = new AtomicLong();

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public HttpServerAioListener() {
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年12月16日 下午5:52:06
	 * 
	 */
	public static void main(String[] args) {
	}

	/** 
	 * @see org.tio.server.intf.ServerAioListener#onAfterAccepted(java.nio.channels.AsynchronousSocketChannel, org.tio.server.AioServer)
	 * 
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:03:45
	 * 
	 */
	//	@Override
	//	public boolean onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, AioServer<ImSessionContext, HttpPacket, Object> aioServer)
	//	{
	//		return true;
	//	}

	@Override
	public void onAfterConnected(ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext, boolean isConnected, boolean isReconnect) {
		HttpSessionContext httpSessionContext = new HttpSessionContext();
		channelContext.setSessionContext(httpSessionContext);

//		GroupContext<HttpSessionContext, HttpPacket, Object> groupContext = channelContext.getGroupContext();
//		log.info(groupContext.toString());
		//		int permitsPerSecond = HttpServerStarter.conf.getInt("request.permitsPerSecond");
		//		int warnClearInterval = 1000 * HttpServerStarter.conf.getInt("request.warnClearInterval");
		//		int maxWarnCount = HttpServerStarter.conf.getInt("request.maxWarnCount");
		//		int maxAllWarnCount = HttpServerStarter.conf.getInt("request.maxAllWarnCount");
		//		RateLimiterWrap rateLimiterWrap = new RateLimiterWrap(permitsPerSecond, warnClearInterval, maxWarnCount, maxAllWarnCount);

		if (isConnected) {
			String ip = channelContext.getClientNode().getIp();

			//			ImUtils.setClient(channelContext);

			AtomicLong ipcount = ipmap.get(ip);
			if (ipcount == null) {
				ipcount = new AtomicLong();
				ipmap.put(ip, ipcount);
			}
			ipcount.incrementAndGet();

			//			String region = StringUtils.leftPad(dataBlock.getRegion(), 12);
			String accessCountStr = StringUtils.leftPad(accessCount.incrementAndGet() + "", 9);
			String ipCountStr = StringUtils.leftPad(ipmap.size() + "", 9);
			String ipStr = StringUtils.leftPad(ip, 15);
			//地区，所有的访问次数，有多少个不同的ip， ip， 这个ip连接的次数
			iplog.info("{} {} {} {}", accessCountStr, ipCountStr, ipStr, ipcount);
		}

		return;
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterSent(ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext, HttpPacket packet, boolean isSentSuccess) {
		//		if (isSentSuccess) {
		//			CommandStat.getCount(packet.getCommand()).sent.incrementAndGet();
		//		}

		HttpResponsePacket httpResponsePacket = (HttpResponsePacket) packet;
//		HttpRequestPacket httpRequestPacket = httpResponsePacket.getHttpRequestPacket();
		
		
		String Connection = httpResponsePacket.getHeader(HttpConst.ResponseHeaderKey.Connection);
		// 现在基本都是1.1了，所以用close来判断
		if (StringUtils.equalsIgnoreCase(Connection, HttpConst.ResponseHeaderValue.Connection.close)) {
			Aio.remove(channelContext, "onAfterSent");
		}
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterReceived(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:08:44
	 * 
	 */
	@Override
	public void onAfterReceived(ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext, HttpPacket packet, int packetSize) {
		//		CommandStat.getCount(packet.getCommand()).received.incrementAndGet();
	}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String)
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author: tanyaowu
	 * 2017年2月1日 上午11:03:11
	 * 
	 */
	@Override
	public void onAfterClose(ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	@Override
	public void onBeforeClose(ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

}
