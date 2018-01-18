package org.tio.http.server;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.intf.ServerAioListener;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpServerAioListener implements ServerAioListener {

	//	private static Logger log = LoggerFactory.getLogger(HttpServerAioListener.class);
	private static Logger iplog = LoggerFactory.getLogger("tio-ip-trace-log");

	static Map<String, AtomicLong> ipmap = new java.util.concurrent.ConcurrentHashMap<>();
	static AtomicLong accessCount = new AtomicLong();

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年12月16日 下午5:52:06
	 *
	 */
	public static void main(String[] args) {
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2016年12月16日 下午5:52:06
	 *
	 */
	public HttpServerAioListener() {
	}

	/**
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String)
	 *
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 * 2017年2月1日 上午11:03:11
	 *
	 */
	@Override
	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	/**
	 * @see org.tio.server.intf.ServerAioListener#onAfterAccepted(java.nio.channels.AsynchronousSocketChannel, org.tio.server.AioServer)
	 *
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:03:45
	 *
	 */
	//	@Override
	//	public boolean onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, AioServer<ImSessionContext, HttpPacket, Object> aioServer)
	//	{
	//		return true;
	//	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		//		HttpSessionContext httpSession = new HttpSessionContext();
		//		channelContext.setAttribute(httpSession);

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
			String accessCountStr = StringUtils.rightPad(accessCount.incrementAndGet() + "", 9);
			String ipCountStr = StringUtils.rightPad(ipmap.size() + "", 9);
			String ipStr = StringUtils.leftPad(ip, 15);
			//地区，所有的访问次数，有多少个不同的ip， ip， 这个ip连接的次数
			iplog.info("总访问次数:{}, 共有{}个不同ip访问, [{}]的访问次数{}, ", accessCountStr, ipCountStr, ipStr, ipcount);
		}

		return;
	}

	/**
	 * @see org.tio.core.intf.AioListener#onAfterReceived(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 *
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author tanyaowu
	 * 2016年12月20日 上午11:08:44
	 *
	 */
	@Override
	public void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) {
		//		CommandStat.getCount(packet.getCommand()).received.incrementAndGet();
	}

	/**
	 * @see org.tio.core.intf.AioListener#onBeforeSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 *
	 * @param channelContext
	 * @param packet
	 * @author tanyaowu
	 * 2016年12月20日 上午11:08:44
	 *
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
		//		if (isSentSuccess) {
		//			CommandStat.getCount(packet.getCommand()).sent.incrementAndGet();
		//		}

		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
		if ((sslFacadeContext == null || sslFacadeContext.isHandshakeCompleted()) && packet instanceof HttpResponse) {
			HttpResponse httpResponse = (HttpResponse) packet;

			String Connection = httpResponse.getHeader(HttpConst.ResponseHeaderKey.Connection);
			// 现在基本都是1.1了，所以用close来判断
			if (StringUtils.equalsIgnoreCase(Connection, HttpConst.ResponseHeaderValue.Connection.close)) {
				HttpRequest request = httpResponse.getHttpRequest();
				String line = request.getRequestLine().getLine();
				Aio.remove(channelContext, "onAfterSent, " + line);
			}
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
		HttpRequest request = (HttpRequest)channelContext.getAttribute(HttpServerAioHandler.REQUEST_KEY);
		if (request != null) {
			request.setClosed(true);
		}
	}

}
