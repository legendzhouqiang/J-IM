package org.tio.http.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.http.common.GroupContextKey;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;

/**
 * @author tanyaowu
 * 2017年8月18日 下午5:47:00
 */
public class HttpServerUtils {
	private static Logger log = LoggerFactory.getLogger(HttpServerUtils.class);

	/**
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public static HttpConfig getHttpConfig(HttpRequest request) {
		ChannelContext channelContext = request.getChannelContext();
		GroupContext groupContext = channelContext.getGroupContext();
		HttpConfig httpConfig = (HttpConfig) groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG);
		return httpConfig;
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public HttpServerUtils() {
	}
}
