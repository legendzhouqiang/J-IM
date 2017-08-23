package org.tio.http.server.handler;

import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;

/**
 *
 * @author tanyaowu
 *
 */
public interface IHttpRequestHandler {
	/**
	 *
	 * @param packet
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse handler(HttpRequest packet, RequestLine requestLine, ChannelContext channelContext) throws Exception;

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine, ChannelContext channelContext);

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @param throwable
	 * @return
	 * @author tanyaowu
	 */
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, ChannelContext channelContext, java.lang.Throwable throwable);
}
