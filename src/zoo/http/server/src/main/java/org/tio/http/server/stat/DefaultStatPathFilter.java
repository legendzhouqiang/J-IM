/**
 * 
 */
package org.tio.http.server.stat;

import org.tio.http.common.HttpRequest;

/**
 * @author tanyw
 *
 */
public class DefaultStatPathFilter implements StatPathFilter {
	
	public static final DefaultStatPathFilter me = new DefaultStatPathFilter();

	/**
	 * 
	 */
	public DefaultStatPathFilter() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	@Override
	public boolean filter(String path, HttpRequest request) {
		return true;
	}

}
