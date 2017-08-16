package org.tio.server;

import org.tio.common.ChannelContext;
import org.tio.common.GroupContext;
import org.tio.common.PacketHandlerMode;
import org.tio.concurrent.SetWithLock;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 服务端分组上下文
 */
public class ServerGroupContext extends GroupContext {

    /** 消息处理模式 */
    private PacketHandlerMode packetHandlerMode = PacketHandlerMode.QUEUE;

    /** 分组监听器 */
    private GroupListener groupListener = null;

    /** 已经连接的 */
    private SetWithLock<ChannelContext> connections;

    /** 断开连接的 */
    private SetWithLock<ChannelContext> closeds;

    /** 黑名单 */
    private Blacklist blacklist;
}
