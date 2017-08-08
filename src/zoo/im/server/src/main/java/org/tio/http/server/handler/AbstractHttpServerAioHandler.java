package org.tio.http.server.handler;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpSessionContext;
import org.tio.http.common.http.HttpRequestDecoder;
import org.tio.http.common.http.HttpRequestPacket;
import org.tio.http.common.http.HttpResponseEncoder;
import org.tio.http.common.http.HttpResponsePacket;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.mvc.Routes;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author tanyaowu 
 *
 */
public abstract class AbstractHttpServerAioHandler implements ServerAioHandler<HttpSessionContext, HttpPacket, Object>,IHttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(AbstractHttpServerAioHandler.class);

	protected HttpServerConfig httpServerConfig;
	
	protected  Routes routes = null;
	public AbstractHttpServerAioHandler(){
		//默认构造器;
	}
	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public AbstractHttpServerAioHandler(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}
	
	public AbstractHttpServerAioHandler(HttpServerConfig httpServerConfig , Routes routes) {
		this(httpServerConfig);
		this.routes = routes;
	}
	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public static void main(String[] args) {
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @throws Exception 
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public Object handler(HttpPacket packet, ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext) throws Exception {
		HttpRequestPacket httpRequestPacket = (HttpRequestPacket) packet;
		HttpResponsePacket httpResponsePacket  = this.handler(httpRequestPacket,  httpRequestPacket.getRequestLine(), channelContext);
		Aio.send(channelContext, httpResponsePacket);
		return null;

	}

	/** 
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ByteBuffer encode(HttpPacket packet, GroupContext<HttpSessionContext, HttpPacket, Object> groupContext,
			ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext) {
		HttpResponsePacket httpResponsePacket = (HttpResponsePacket) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponsePacket, groupContext, channelContext);
		return byteBuffer;
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 * 
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public HttpRequestPacket decode(ByteBuffer buffer, ChannelContext<HttpSessionContext, HttpPacket, Object> channelContext) throws AioDecodeException {
		HttpRequestPacket httpRequestPacket = HttpRequestDecoder.decode(buffer, channelContext);
		return httpRequestPacket;
	}

	/**
	 * @return the httpServerConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpServerConfig;
	}

	/**
	 * @param httpServerConfig the httpServerConfig to set
	 */
	public void setHttpServerConfig(HttpServerConfig httpServerConfig) {
		this.httpServerConfig = httpServerConfig;
	}

}
