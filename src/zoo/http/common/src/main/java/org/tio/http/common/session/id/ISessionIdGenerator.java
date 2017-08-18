package org.tio.http.common.session.id;

import org.tio.http.common.HttpConfig;

/**
 * @author tanyaowu
 * 2017年8月15日 上午10:49:58
 */
public interface ISessionIdGenerator {

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	String sessionId(HttpConfig httpConfig);

}
