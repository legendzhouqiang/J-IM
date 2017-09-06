package org.tio.common.etc;

import org.tio.common.ChannelContext;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io监听器
 */
public interface TioLinkListener {

    /**
     * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
     *
     * @param channelContext
     */
    void onAfterConnected(ChannelContext channelContext) throws Exception;

    /**
     * 连接关闭前触发本方法
     *
     * @param channelContext
     * @param throwable
     */
    void onBeforeClose(ChannelContext channelContext, Throwable throwable);

    /**
     * 连接关闭前后触发本方法
     *
     * @param channelContext
     * @param throwable
     */
    void onAfterClosed(ChannelContext channelContext, Throwable throwable) throws Exception;
}
