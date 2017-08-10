package org.tio.common;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 分组监听器
 */
public interface GroupListener {
    /**
	 * 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterBind(ChannelContext channelContext, String group) throws Exception;

	/**
	 * 
	 * @param channelContext
	 * @param group
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterUnbind(ChannelContext channelContext, String group) throws Exception;
}
