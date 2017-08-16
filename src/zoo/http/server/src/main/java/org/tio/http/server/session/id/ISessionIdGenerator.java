package org.tio.http.server.session.id;

import org.tio.http.server.HttpServerConfig;

/**
 * @author tanyaowu 
 * 2017年8月15日 上午10:49:58
 */
public interface ISessionIdGenerator {
	
	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	String sessionId(HttpServerConfig httpConfig);

}
