/**
 * 
 */
package org.tio.http.server.intf;

import org.tio.http.common.HttpRequest;

/**
 * @author tanyw
 *
 */
public interface CurrUseridGetter {
	
	/**
	 * 根据HttpRequest获取当前用户的userid
	 * @param request
	 * @return
	 */
	public String getUserid(HttpRequest request);

}
