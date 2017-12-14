package org.tio.websocket.server.demo1;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.server.handler.IWsMsgHandler;

/**
 * @author tanyaowu
 * 2017年6月28日 下午5:32:38
 */
public class WsDemoMsgHandler implements IWsMsgHandler {
	private static Logger log = LoggerFactory.getLogger(WsDemoMsgHandler.class);

	/**
	 * 握手时走这个方法，业务可以在这里获取cookie，request参数等
	 */
	@Override
	public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
		request.getClientIp();
		return httpResponse;
	}

	/**
	 * 字节消息（binaryType = arraybuffer）过来后会走这个方法
	 */
	@Override
	public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
		String ss = new String(bytes, "utf-8");
		log.info("收到byte消息:{},{}", bytes, ss);
		channelContext.getClientNode().getIp();

		//		byte[] bs1 = "收到byte消息".getBytes("utf-8");
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);

		return buffer;
	}

	/**
	 * 当客户端发close flag时，会走这个方法
	 */
	@Override
	public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
		Aio.remove(channelContext, "receive close flag");
		return null;
	}

	/**
	 * 字符消息（binaryType = blob）过来后会走这个方法
	 */
	@Override
	public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
		return "收到text消息:" + text;
	}
}
