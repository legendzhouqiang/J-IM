package org.tio.runnable;

import org.tio.common.ChannelContext;
import org.tio.common.Packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class SendTaskQueue extends AbstractTaskQueue<Packet> {

    private ChannelContext context;

    public SendTaskQueue(ChannelContext context) {
        this.context = context;
    }

    @Override
    public void runTask(Packet packet) {

    }
}
