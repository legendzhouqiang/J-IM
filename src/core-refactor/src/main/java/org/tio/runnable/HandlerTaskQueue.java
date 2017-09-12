package org.tio.runnable;

import org.tio.common.ChannelContextImpl;
import org.tio.common.packet.SuperPacket;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class HandlerTaskQueue extends AbstractTaskQueue<SuperPacket> {

    private ChannelContextImpl context;

    public HandlerTaskQueue(ChannelContextImpl context) {
        this.context = context;
    }

    @Override
    public void runTask(SuperPacket packet) {

    }
}
