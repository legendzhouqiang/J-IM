package org.tio.http.server.session;

import org.tio.http.common.Cookie;

/**
 * @author tanyaowu 
 * 2017年10月11日 下午2:40:19
 */
public interface SessionCookieDecorator {
	/**
	 * DefaultHttpRequestHandler根据host字段创建了用于session的cookie，用户可以通过本方法定制一下Cookie，
	 * 譬如把cookie的域名由www.baidu.com改成.baidu.com
	 * @param sessionCookie
	 * @author: tanyaowu
	 */
	public void decorate(Cookie sessionCookie);
}
