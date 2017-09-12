package org.tio.server;

import org.tio.client.ClientChannelContext;
import org.tio.common.*;
import org.tio.concurrent.SetWithLock;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 服务端分组上下文
 */
public class ServerGroupContext extends GroupContext {

    /** 消息处理模式 */
    private PacketHandlerMode packetHandlerMode = PacketHandlerMode.POOL;

    /** 已经连接的:高活 */
    private SetWithLock<ChannelContextImpl> highActiveConnections;
    /** 已经连接的:活越 */
    private SetWithLock<ChannelContextImpl> activeConnections;
    /** 已经连接的:低活 */
    private SetWithLock<ChannelContextImpl> commonConnections;

    /** 断开连接的 */
    private SetWithLock<ChannelContextImpl> closeds;

    private ConcurrentHashMap<Node, ClientChannelContext> nodeForChennelMap = new ConcurrentHashMap<>();

    /** 黑名单 */
    private Blacklist blacklist;


    public void start(){
        if (status.equals(CoreConstant.Status.Init)) {
            status = CoreConstant.Status.STARTING;

            status = CoreConstant.Status.RUNING;
        }
    }

    public void stop(){
        if (status.equals(CoreConstant.Status.RUNING)) {
            status = CoreConstant.Status.STARTING;

            status = CoreConstant.Status.STOPPED;
        }

    }
}
