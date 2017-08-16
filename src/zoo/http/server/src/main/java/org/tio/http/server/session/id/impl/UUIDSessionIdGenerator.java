package org.tio.http.server.session.id.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.session.id.ISessionIdGenerator;

import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * @author tanyaowu 
 * 2017年8月15日 上午10:53:39
 */
public class UUIDSessionIdGenerator implements ISessionIdGenerator {
	private static Logger log = LoggerFactory.getLogger(UUIDSessionIdGenerator.class);
	
	public final static UUIDSessionIdGenerator instance = new UUIDSessionIdGenerator();

	/**
	 * 
	 * @author: tanyaowu
	 */
	private UUIDSessionIdGenerator() {
	}

	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public String sessionId(HttpServerConfig httpConfig) {
		return RandomUtil.randomUUID().replace("-", "");
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {
		UUIDSessionIdGenerator uuidSessionIdGenerator = new UUIDSessionIdGenerator();
		String xx = uuidSessionIdGenerator.sessionId(null);
		System.out.println(xx);

	}
}
