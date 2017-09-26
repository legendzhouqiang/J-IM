package org.tio.core.intf;

import org.tio.core.stat.IpStat;

/**
 * @author tanyaowu 
 * 2017年9月25日 下午4:40:27
 */
public interface IpStatListener {
	/**
	 * 统计时间段到期后，用户可以在这个方法中实现把相关数据入库或是打日志等
	 * @param ipStat
	 * @author: tanyaowu
	 */
	public void onExpired(IpStat ipStat);

}
