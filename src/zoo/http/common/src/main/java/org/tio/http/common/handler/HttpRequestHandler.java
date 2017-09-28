package org.tio.http.common.handler;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;

/**
 *
 * @author tanyaowu
 *
 */
public interface HttpRequestHandler {
	/**
	 *
	 * @param packet
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse handler(HttpRequest packet) throws Exception;

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine);

	/**
	 *
	 * @param request
	 * @param requestLine
	 * @param throwable
	 * @return
	 * @author tanyaowu
	 */
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, java.lang.Throwable throwable);
	
	/**
	 * 清空静态资源缓存，如果没有缓存，可以不处理
	 * @param request
	 * @author: tanyaowu
	 */
	public void clearStaticResCache(HttpRequest request);
}
