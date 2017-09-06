package org.tio.common.etc;

import org.tio.common.ChannelContext;
import org.tio.common.packet.SuperPacket;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据交互监听器
 */
public interface TioInteractiveListener {

    /**
     * 消息包发送之前触发本方法
     *
     * @param channelContext
     * @param packet
     */
    void onBeforeSent(ChannelContext channelContext, SuperPacket packet) throws TioException;

    /**
     * 消息包发送之后触发本方法
     *
     * @param channelContext
     * @param packet
     */
    void onAfterSent(ChannelContext channelContext, SuperPacket packet) throws TioException;

    /**
     * 解码前触发本方法
     *
     * @param channelContext
     * @param packet
     */
    void onBeforeReceived(ChannelContext channelContext, SuperPacket packet) throws TioException;

    /**
     * 解码成功后触发本方法
     *
     * @param channelContext
     * @param packet
     */
    void onAfterReceive(ChannelContext channelContext, SuperPacket packet) throws TioException;

}
