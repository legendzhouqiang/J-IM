package org.tio.http.server.session;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.Cookie;

import cn.hutool.core.util.ReUtil;

/**
 * @author tanyaowu 
 * 2017年10月11日 下午2:59:10
 */
public class DomainSessionCookieDecorator implements SessionCookieDecorator {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(DomainSessionCookieDecorator.class);

	/**
	 * 形如:".baidu.com"
	 */
	private String domain;
	
	private DomainMappingSessionCookieDecorator domainMappingSessionCookieDecorator;

	/**
	 * 
	 * @param domain 形如:".baidu.com"
	 * @author: tanyaowu
	 */
	public DomainSessionCookieDecorator(String domain) {
		this.domain = domain;
		
		Map<String, String> domainMap = new HashMap<>();
		domainMap.put("(\\w)*(" + domain + "){1}", domain);

		domainMappingSessionCookieDecorator = new DomainMappingSessionCookieDecorator(domainMap);
	}



	/** 
	 * @param sessionCookie
	 * @author: tanyaowu
	 */
	@Override
	public void decorate(Cookie sessionCookie) {
		domainMappingSessionCookieDecorator.decorate(sessionCookie);
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {
		boolean bb = ReUtil.isMatch("(\\w)*(.baidu.com){1}", "www.baidu.com");
		System.out.println(bb);

		Cookie sessionCookie = new Cookie();
		sessionCookie.setDomain("www.baidu.com");
		
		System.out.println(sessionCookie.getDomain());

		DomainSessionCookieDecorator domainSessionCookieDecorator = new DomainSessionCookieDecorator(".baidu.com");
		domainSessionCookieDecorator.decorate(sessionCookie);
		
		System.out.println(sessionCookie.getDomain());
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
}
