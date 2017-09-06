package org.tio.server;

import org.tio.client.ClientChannelContext;
import org.tio.common.*;
import org.tio.concurrent.SetWithLock;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 服务端分组上下文
 */
public class ServerGroupContext extends GroupContext {

    /** 消息处理模式 */
    private PacketHandlerMode packetHandlerMode = PacketHandlerMode.QUEUE;

    /** 已经连接的:高活 */
    private SetWithLock<ChannelContext> highActiveConnections;
    /** 已经连接的:活越 */
    private SetWithLock<ChannelContext> activeConnections;
    /** 已经连接的:低活 */
    private SetWithLock<ChannelContext> commonConnections;

    /** 断开连接的 */
    private SetWithLock<ChannelContext> closeds;

    private ConcurrentHashMap<Node, ClientChannelContext> nodeForChennelMap = new ConcurrentHashMap<>();

    /** 黑名单 */
    private Blacklist blacklist;
}
