package org.tio.websocket.server.handler;

import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;

/**
 *
 * @author tanyaowu
 * 2017年7月30日 上午9:34:59
 */
public interface IWsMsgHandler {
	/**
	 * 对httpResponse参数进行补充并返回，如果返回null表示不想和对方建立连接，框架会断开连接，如果返回非null，框架会把这个对象发送给对方
	 * @param httpRequest
	 * @param httpResponse
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception;

	/**
	 *
	 * @param wsRequest
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 *
	 * @param wsRequest
	 * @param bytes
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception;

	/**
	 * @param wsRequest
	 * @param text
	 * @param channelContext
	 * @return 可以是WsResponse、byte[]、ByteBuffer、String或null，如果是null，框架不会回消息
	 * @throws Exception
	 * @author tanyaowu
	 */
	Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception;
}
