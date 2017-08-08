package org.tio.http.server.session.impl;

import org.redisson.api.RedissonClient;
import org.tio.core.GroupContext;
import org.tio.http.server.session.IHttpSessionFactory;

/**
 * 
 * @author tanyaowu 
 * 2017年8月5日 上午11:58:43
 */
public class RedisHttpSessionFactory implements IHttpSessionFactory<RedisHttpSession> {

	public static RedisHttpSessionFactory instance = null;//new RedisHttpSessionFactory();

	private RedissonClient redisson;

	private RedisHttpSessionFactory(RedissonClient redisson) {

	}

	public static RedisHttpSessionFactory getInstance(RedissonClient redisson) {
		if (instance == null) {
			synchronized (RedisHttpSessionFactory.class) {
				if (instance == null) {
					instance = new RedisHttpSessionFactory(redisson);
				}
			}
		}
		return instance;
	}

	@Override
	public RedisHttpSession create(String sessionId, Long sessionTimeout, GroupContext groupContext) {
		return new RedisHttpSession(sessionId, sessionTimeout, redisson);
	}
}
