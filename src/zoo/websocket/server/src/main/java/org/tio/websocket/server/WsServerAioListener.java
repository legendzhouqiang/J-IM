package org.tio.websocket.server;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;
import org.tio.websocket.common.WsSessionContext;

/**
 * 
 * @author tanyaowu 
 * 2017年7月30日 上午9:16:02
 */
public class WsServerAioListener implements ServerAioListener {

	//	private static Logger log = LoggerFactory.getLogger(WsServerAioListener.class);
	//	private static Logger iplog = LoggerFactory.getLogger("tio-ip-trace-log");

	static Map<String, AtomicLong> ipmap = new java.util.concurrent.ConcurrentHashMap<>();
	static AtomicLong accessCount = new AtomicLong();

	public WsServerAioListener() {
	}

	public static void main(String[] args) {
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		WsSessionContext wsSessionContext = new WsSessionContext();
		channelContext.setAttribute(wsSessionContext);
		return;
	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
	}

	@Override
	public void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) {

	}

	@Override
	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

}
