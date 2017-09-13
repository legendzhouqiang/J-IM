package org.tio.common.misc;

import org.tio.common.channel.Channel;

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
     * @param channel
     */
    void onAfterConnected(Channel channel) throws TioException;

    /**
     * 连接关闭前触发本方法
     *
     * @param channel
     * @param throwable
     */
    void onBeforeClose(Channel channel, Throwable throwable) throws TioException;

    /**
     * 连接关闭前后触发本方法
     *
     * @param channel
     * @param throwable
     */
    void onAfterClosed(Channel channel, Throwable throwable) throws TioException;
}
