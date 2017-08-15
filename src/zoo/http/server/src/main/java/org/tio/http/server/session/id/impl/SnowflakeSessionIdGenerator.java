package org.tio.http.server.session.id.impl;

import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.session.id.ISessionIdGenerator;

import com.xiaoleilu.hutool.lang.Snowflake;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * @author tanyaowu 
 * 2017年8月15日 上午10:58:22
 */
public class SnowflakeSessionIdGenerator implements ISessionIdGenerator {
	
	private Snowflake snowflake;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public SnowflakeSessionIdGenerator(int workerId, int datacenterId) {
		snowflake = new Snowflake(workerId, datacenterId);
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public SnowflakeSessionIdGenerator() {
		snowflake = new Snowflake(RandomUtil.randomInt(0, 31), RandomUtil.randomInt(0, 31));
	}

	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public String sessionId(HttpServerConfig httpServerConfig) {
		return snowflake.nextId() + "";
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
