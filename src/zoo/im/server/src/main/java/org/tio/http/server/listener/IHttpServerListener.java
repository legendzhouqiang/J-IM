package org.tio.http.server.listener;

import org.tio.core.ChannelContext;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpSessionContext;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.http.common.http.RequestLine;

/**
 * @author tanyaowu 
 * 2017年7月25日 下午2:16:06
 */
public interface IHttpServerListener {

	/**
	 * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler(HttpRequestPacket, RequestLine, ChannelContext<HttpSessionContext, HttpPacket, Object>)前会先调用这个方法<br>
	 * 如果返回了HttpResponsePacket对象，则后续都不再执行，表示调用栈就此结束<br>
	 * @param packet
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public HttpResponsePacket doBeforeHandler(HttpRequestPacket packet, RequestLine requestLine, ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext)
			throws Exception;

	/**
	 * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler(HttpRequestPacket, RequestLine, ChannelContext<HttpSessionContext, HttpPacket, Object>)后会调用此方法，业务层可以统一在这里给HttpResponsePacket作一些修饰
	 * @param packet
	 * @param requestLine
	 * @param channelContext
	 * @param httpResponsePacket
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public void doAfterHandler(HttpRequestPacket packet, RequestLine requestLine, ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext,
			HttpResponsePacket httpResponsePacket) throws Exception;

}
