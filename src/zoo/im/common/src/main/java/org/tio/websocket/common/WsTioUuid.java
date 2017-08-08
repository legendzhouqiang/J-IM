package org.tio.websocket.common;

import org.tio.core.intf.TioUuid;

import com.xiaoleilu.hutool.lang.Snowflake;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * @author tanyaowu 
 * 2017年6月5日 上午10:44:26
 */
public class WsTioUuid implements TioUuid {
	private Snowflake snowflake;

	public WsTioUuid(long workerId, long datacenterId) {
		snowflake = new Snowflake(workerId, datacenterId);
	}

	public WsTioUuid() {
		snowflake = new Snowflake(RandomUtil.randomInt(1, 30), RandomUtil.randomInt(1, 30));
	}

	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public String uuid() {
		return snowflake.nextId() + "";
	}
}
