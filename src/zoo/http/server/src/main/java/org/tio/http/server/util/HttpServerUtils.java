package org.tio.http.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.http.common.GroupContextKey;
import org.tio.http.common.HttpRequest;
import org.tio.http.server.HttpServerConfig;

/**
 * @author tanyaowu 
 * 2017年8月18日 下午5:47:00
 */
public class HttpServerUtils {
	private static Logger log = LoggerFactory.getLogger(HttpServerUtils.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public HttpServerUtils() {
	}
	
	public static HttpServerConfig getHttpConfig(HttpRequest request) {
		ChannelContext channelContext = request.getChannelContext();
		GroupContext groupContext = channelContext.getGroupContext();
		HttpServerConfig httpServerConfig = (HttpServerConfig)groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG);
		return httpServerConfig;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
