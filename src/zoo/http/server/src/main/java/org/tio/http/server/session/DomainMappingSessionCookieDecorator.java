package org.tio.http.server.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.Cookie;

import cn.hutool.core.util.ReUtil;

/**
 * @author tanyaowu 
 * 2017年10月11日 下午2:59:10
 */
public class DomainMappingSessionCookieDecorator implements SessionCookieDecorator {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(DomainMappingSessionCookieDecorator.class);

	/**
	 * key:    原始domain，譬如:www.baidu.com，也可以是正则表达式，譬如*.baidu.com
	 * value : 替换原始domain的domain，譬如.baidu.com
	 */
	private Map<String, String> domainMap = null;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public DomainMappingSessionCookieDecorator(Map<String, String> domainMap) {
		this.domainMap = domainMap;
	}
	
	protected DomainMappingSessionCookieDecorator() {
		
	}

	public void addMapping(String key, String value) {
		domainMap.put(key, value);
	}

	public void removeMapping(String key) {
		domainMap.remove(key);
	}

	/** 
	 * @param sessionCookie
	 * @author: tanyaowu
	 */
	@Override
	public void decorate(Cookie sessionCookie) {
		Set<Entry<String, String>> set = domainMap.entrySet();
		String initDomain = sessionCookie.getDomain();
		for (Entry<String, String> entry : set) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (StringUtils.equalsIgnoreCase(key, initDomain) || ReUtil.isMatch(key, initDomain)) {
				sessionCookie.setDomain(value);
			}
		}
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
		
		Map<String, String> domainMap = new HashMap<>();
		domainMap.put("(\\w)*(.baidu.com){1}", ".baidu.com");
		
		DomainMappingSessionCookieDecorator domainMappingSessionCookieDecorator = new DomainMappingSessionCookieDecorator(domainMap);
		domainMappingSessionCookieDecorator.decorate(sessionCookie);
		
		System.out.println(sessionCookie.getDomain());
	}
}
