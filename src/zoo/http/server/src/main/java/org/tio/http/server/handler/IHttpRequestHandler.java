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
public interface IHttpRequestHandler
{
	/**
	 * 
	 * @param packet
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public HttpResponse handler(HttpRequest packet, RequestLine requestLine, ChannelContext channelContext)  throws Exception;
	
	/**
	 * 
	 * @param httpRequest
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public HttpResponse resp404(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext);
	
	/**
	 * 
	 * @param httpRequest
	 * @param requestLine
	 * @param channelContext
	 * @param throwable
	 * @return
	 * @author: tanyaowu
	 */
	public HttpResponse resp500(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext, java.lang.Throwable throwable);
}
