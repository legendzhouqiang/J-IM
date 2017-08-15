package org.tio.http.server;

import java.nio.ByteBuffer;

import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpRequestDecoder;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.http.server.handler.IHttpRequestHandler;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpServerAioHandler implements ServerAioHandler {
	//	private static Logger log = LoggerFactory.getLogger(HttpServerAioHandler.class);

	protected HttpServerConfig httpServerConfig;

	private IHttpRequestHandler httpRequestHandler;

	//	protected Routes routes = null;

	//	public HttpServerAioHandler(IHttpRequestHandler httpRequestHandler) {
	//		this.httpRequestHandler = httpRequestHandler;
	//	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public HttpServerAioHandler(HttpServerConfig httpServerConfig, IHttpRequestHandler httpRequestHandler) {
		this.httpServerConfig = httpServerConfig;
		this.httpRequestHandler = httpRequestHandler;
	}

	//	public HttpServerAioHandler(HttpServerConfig httpServerConfig, IHttpRequestHandler httpRequestHandler) {
	//		this(httpServerConfig, httpRequestHandler);
	////		this.routes = routes;
	//	}

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
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		HttpRequest httpRequest = (HttpRequest) packet;
		HttpResponse httpResponse = httpRequestHandler.handler(httpRequest, httpRequest.getRequestLine(), channelContext);
		Aio.send(channelContext, httpResponse);
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
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		HttpResponse httpResponse = (HttpResponse) packet;
		ByteBuffer byteBuffer = HttpResponseEncoder.encode(httpResponse, groupContext, channelContext, false);
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
	public HttpRequest decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		HttpRequest httpRequest = HttpRequestDecoder.decode(buffer, channelContext);
		return httpRequest;
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
