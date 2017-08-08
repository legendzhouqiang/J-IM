package org.tio.http.server.session.impl;

import org.tio.core.GroupContext;
import org.tio.http.server.session.IHttpSessionFactory;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午11:58:43
 */
public class HashMapHttpSessionFactory implements IHttpSessionFactory<HashMapHttpSession> {

	public static HashMapHttpSessionFactory self = new HashMapHttpSessionFactory();

	private HashMapHttpSessionFactory() {
	}

	/**
	 * 
	 * @param serverGroupContext
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public HashMapHttpSession create(String sessionId, Long sessionTimeout, GroupContext groupContext) {
		return new HashMapHttpSession();
	}
}
