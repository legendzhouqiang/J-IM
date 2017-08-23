package org.tio.core.intf;

import org.tio.core.ChannelContext;

/**
 * @author tanyaowu
 * 2017年5月13日 下午10:35:05
 */
public interface GroupListener {
	/**
	 *
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	void onAfterBind(ChannelContext channelContext, String group) throws Exception;

	/**
	 *
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author tanyaowu
	 */
	void onAfterUnbind(ChannelContext channelContext, String group) throws Exception;
}
