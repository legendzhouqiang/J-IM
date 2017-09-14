package org.tio.runnable;

import org.tio.common.channel.Channel;
import org.tio.common.packet.SuperPacket;
import org.tio.runnable.common.AbstractTaskQueue;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class HandlerTaskQueue extends AbstractTaskQueue<SuperPacket> {

    private Channel context;

    public HandlerTaskQueue(Channel context) {
        this.context = context;
    }

    @Override
    public void runTask(SuperPacket packet) {

    }
}
