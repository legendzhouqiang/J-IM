package org.tio.http.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

import cn.hutool.core.util.ZipUtil;

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
//	public static HttpConfig getHttpConfig(HttpRequest request) {
//		ChannelContext channelContext = request.getChannelContext();
//		GroupContext groupContext = channelContext.getGroupContext();
//		HttpConfig httpConfig = (HttpConfig) groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG);
//		return httpConfig;
//	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @author tanyaowu
	 */
	public static void gzip(HttpRequest request, HttpResponse response) {
		if (response == null) {
			return;
		}
		
		// 已经gzip过了，就不必再压缩了
		if (response.isHasGzipped()) {
			return;
		}
		
		if (request.getIsSupportGzip()) {
			byte[] bs = response.getBody();
			if (bs != null && bs.length >= 300) {
				byte[] bs2 = ZipUtil.gzip(bs);
				if (bs2.length < bs.length) {
					response.setBody(bs2);
					response.setHasGzipped(true);
					response.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding, "gzip");
				}
			}
		} else {
			log.warn("{}, 不支持gzip, {}", request.getClientIp(), request.getHeader(HttpConst.RequestHeaderKey.User_Agent));
		}
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
