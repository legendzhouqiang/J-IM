package org.tio.http.server.handler;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpRequestDecoder;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.server.mvc.Routes;
import org.tio.server.intf.ServerAioHandler;

/**
 *
 * @author tanyaowu
 *
 */
public abstract class AbstractHttpServerAioHandler implements ServerAioHandler, HttpRequestHandler {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AbstractHttpServerAioHandler.class);

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public static void main(String[] args) {
	}

	protected HttpConfig httpConfig;

	protected Routes routes = null;

	public AbstractHttpServerAioHandler() {
		//默认构造器;
	}

	/**
	 * 
	 * @param httpConfig
	 */
	public AbstractHttpServerAioHandler(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public AbstractHttpServerAioHandler(HttpConfig httpConfig, Routes routes) {
		this(httpConfig);
		this.routes = routes;
	}

	@Override
	public HttpRequest decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		HttpRequest request = HttpRequestDecoder.decode(buffer, channelContext);
		return request;
	}

	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		HttpResponse httpResponse = (HttpResponse) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponse, groupContext, channelContext, false);
		return byteBuffer;
	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HttpRequest request = (HttpRequest) packet;
		HttpResponse httpResponse = this.handler(request);
		Aio.send(channelContext, httpResponse);
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

}
