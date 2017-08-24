package org.tio.client;

import org.tio.common.ChannelContext;
import org.tio.common.GroupContext;
import org.tio.common.PacketHandlerMode;
import org.tio.concurrent.SetWithLock;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc:
 */
public class ClientGroupContext extends GroupContext {

    /** 消息处理模式 */
    private PacketHandlerMode packetHandlerMode = PacketHandlerMode.SINGLE_THREAD;
    /** 重连配置 */
    protected ReconnectConfig reconnConf;

    /** 建立的连接的 */
    private SetWithLock<ChannelContext> connections;


}
