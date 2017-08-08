package org.tio.websocket.server.demo1;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.websocket.common.WsPacket;
import org.tio.websocket.common.WsRequestPacket;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.handler.IWsMsgHandler;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午5:32:38
 */
public class WsMsgHandler implements IWsMsgHandler {
	private static Logger log = LoggerFactory.getLogger(WsMsgHandler.class);

	@Override
	public HttpResponsePacket handshake(HttpRequestPacket httpRequestPacket, HttpResponsePacket httpResponsePacket,
			ChannelContext<WsSessionContext, WsPacket, Object> channelContext) throws Exception {
		return httpResponsePacket;
	}

	@Override
	public Object onText(WsRequestPacket wsRequestPacket, String text, ChannelContext<WsSessionContext, WsPacket, Object> channelContext) throws Exception {
		return "收到text消息:" + text;
	}

	@Override
	public Object onBytes(WsRequestPacket wsRequestPacket, byte[] bytes, ChannelContext<WsSessionContext, WsPacket, Object> channelContext) throws Exception {
		String ss = new String(bytes, "utf-8");
		log.info("收到byte消息:{},{}", bytes, ss);
		
//		byte[] bs1 = "收到byte消息".getBytes("utf-8");
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		
		
		return buffer;
	}

	@Override
	public Object onClose(WsRequestPacket websocketPacket, byte[] bytes, ChannelContext<WsSessionContext, WsPacket, Object> channelContext) throws Exception {
		Aio.remove(channelContext, "receive close flag");
		return null;
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public WsMsgHandler() {}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
