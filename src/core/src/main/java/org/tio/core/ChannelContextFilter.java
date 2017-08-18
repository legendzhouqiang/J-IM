package org.tio.core;

public interface ChannelContextFilter {

	/**
	 *
	 * @param channelContext
	 * @return false: 排除此channelContext, true: 不排除
	 *
	 * @author tanyaowu
	 * 2017年1月13日 下午3:28:54
	 *
	 */
	public boolean filter(ChannelContext channelContext);

}
