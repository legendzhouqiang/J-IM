/**
 * 
 */
package org.tio.http.server.stat.token;

import org.tio.http.common.HttpRequest;

/**
 * @author tanyw
 */
public class DefaultTokenGetter implements TokenGetter {

	public static DefaultTokenGetter me = new DefaultTokenGetter();

	/**
	 * 
	 */
	private DefaultTokenGetter() {
	}

	@Override
	public String getToken(HttpRequest request) {
		return request.getHttpSession().getId();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
