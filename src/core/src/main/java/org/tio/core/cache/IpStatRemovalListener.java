package org.tio.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.stat.IpStat;
import org.tio.json.Json;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @author tanyaowu 
 * 2017年8月21日 下午1:32:32
 */
@SuppressWarnings("rawtypes")
public class IpStatRemovalListener implements RemovalListener {
	private static Logger log = LoggerFactory.getLogger(IpStatRemovalListener.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public IpStatRemovalListener() {
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	@Override
	public void onRemoval(RemovalNotification notification) {
		String ip = (String) notification.getKey();
		IpStat ipStat = (IpStat) notification.getValue();

		log.info("ip数据统计[{}]\r\n{}", ip, Json.toFormatedJson(ipStat));
	}
}
