package org.tio.core.stat;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.tio.utils.time.Time;

/**
 * @author tanyaowu
 * 2017年6月28日 下午2:23:16
 */
public enum IpStatType {
	DAY_1(Time.DAY_1), MINUTE_1(Time.MINUTE_1), MINUTE_10(Time.MINUTE_1 * 10);
	public static IpStatType from(String type) {
		return from(type, null);
	}

	public static IpStatType from(String type, IpStatType defaultIpStatType) {
		if (StringUtils.isBlank(type)) {
			return defaultIpStatType;
		}
		IpStatType[] values = IpStatType.values();
		for (IpStatType v : values) {
			if (Objects.equals(v.name(), type)) {
				return v;
			}
		}
		return defaultIpStatType;
	}

	Long timeToLiveSeconds;

	public Long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	private IpStatType(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
}