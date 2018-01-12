/**
 * 
 */
package org.tio.http.server.stat;

import org.tio.http.common.HttpRequest;

/**
 * @author tanyw
 *
 */
public interface StatPathFilter {
	
	/**
	 * 
	 * @param path
	 * @param request
	 * @return true: 表示要统计， false: 不统计
	 */
	public boolean filter(String path, HttpRequest request);
}
