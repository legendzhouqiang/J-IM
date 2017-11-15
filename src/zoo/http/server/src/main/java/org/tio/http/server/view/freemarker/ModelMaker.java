package org.tio.http.server.view.freemarker;

import org.tio.http.common.HttpRequest;

/**
 * 
 * @author tanyaowu 
 * 2017年11月15日 下午1:12:39
 */
public interface ModelMaker {
	
	/**
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	Object maker(HttpRequest request);

}
