package org.tio.runnable;

import org.tio.common.ChannelContext;
import org.tio.common.Packet;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/20
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:  t-io解码器
 */
public class DecodeTaskQueue extends AbstractTaskQueue<ByteBuffer> {

    private ChannelContext context;

    public DecodeTaskQueue(ChannelContext context) {
        this.context = context;
    }

    @Override
    public void runTask(ByteBuffer byteBuffer) {

        short magic = byteBuffer.getShort();
        while (Packet.magic != magic) {
            magic = byteBuffer.getShort();
        }

        byte packetType = byteBuffer.get();


        boolean flag = msgQueue.offerFirst(byteBuffer);

        Packet p = null;
        context.getHandlerRunnable().addMsg(p);
    }
}
