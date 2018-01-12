/**
 * 
 */
package org.tio.http.server.stat.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.Cookie;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.session.HttpSession;
import org.tio.http.server.handler.DefaultHttpRequestHandler;

/**
 * @author tanyw
 */
public class DefaultTokenGetter implements TokenGetter {
	private static Logger log = LoggerFactory.getLogger(DefaultTokenGetter.class);

	public static DefaultTokenGetter me = new DefaultTokenGetter();

	/**
	 * 
	 */
	private DefaultTokenGetter() {
	}

	@Override
	public String getToken(HttpRequest request) {
		HttpSession httpSession = request.getHttpSession();
		if (httpSession != null) {
			return httpSession.getId();
		}
		Cookie cookie = DefaultHttpRequestHandler.getSessionCookie(request, request.getHttpConfig());
		if (cookie != null) {
			log.error("token from cookie: {}", cookie.getValue());
			return cookie.getValue();
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
