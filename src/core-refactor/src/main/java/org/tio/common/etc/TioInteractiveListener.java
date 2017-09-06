package org.tio.common.etc;

import org.tio.common.ChannelContext;
import org.tio.common.packet.ReadPacket;

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
     * @param context
     * @param packet
     */
    void onBeforeSent(ChannelContext context, ReadPacket packet) throws TioException;

    /**
     * 消息包发送之后触发本方法
     *
     * @param context
     * @param packet
     */
    void onAfterSent(ChannelContext context, ReadPacket packet) throws TioException;

    /**
     * 解码前触发本方法
     *
     * @param context
     * @param packet
     */
    void onBeforeReceived(ChannelContext context, ReadPacket packet) throws TioException;

    /**
     * 解码成功后触发本方法
     *
     * @param context
     * @param packet
     */
    void onAfterReceive(ChannelContext context, ReadPacket packet) throws TioException;

}
