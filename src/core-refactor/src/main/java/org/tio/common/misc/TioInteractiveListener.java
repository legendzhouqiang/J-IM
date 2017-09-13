package org.tio.common.misc;

import org.tio.common.ChannelContextImpl;
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
    void onBeforeSent(ChannelContextImpl context, ReadPacket packet) throws TioException;

    /**
     * 消息包发送之后触发本方法
     *
     * @param context
     * @param packet
     */
    void onAfterSent(ChannelContextImpl context, ReadPacket packet) throws TioException;

    /**
     * 解码前触发本方法
     *
     * @param context
     * @param packet
     */
    void onBeforeReceived(ChannelContextImpl context, ReadPacket packet) throws TioException;

    /**
     * 解码成功后触发本方法
     *
     * @param context
     * @param packet
     */
    void onAfterReceive(ChannelContextImpl context, ReadPacket packet) throws TioException;

}
