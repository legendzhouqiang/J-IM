package org.tio.http.server;

import org.tio.http.common.HttpConfig;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午2:42:59
 */
public class HttpServerConfig extends HttpConfig {

	public HttpServerConfig(Integer bindPort, Long sessionTimeout) {
		super(bindPort, sessionTimeout);
	}
}
