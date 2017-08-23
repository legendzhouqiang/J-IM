package org.tio.utils.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年8月16日 上午10:27:26
 */
public class Time {
	private static Logger log = LoggerFactory.getLogger(Time.class);

	public static final Long SECOND_1 = 1L;

	public static final Long MINUTE_1 = SECOND_1 * 60L;

	public static final Long HOUR_1 = MINUTE_1 * 60L;

	public static final Long DAY_1 = HOUR_1 * 24;

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public Time() {
	}
}
