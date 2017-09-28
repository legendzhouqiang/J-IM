package org.tio.http.server.listener;

import org.tio.http.common.HttpConfig;
import org.tio.http.common.session.HttpSession;

/**
 * @author tanyaowu 
 * 2017年9月27日 下午1:46:20
 */
public interface HttpSessionListener {
	/**
	 * 
	 * @param session
	 * @author: tanyaowu
	 */
	public void doAfterCreated(HttpSession session, HttpConfig httpConfig);

}
