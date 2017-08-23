package org.tio.runnable;

import org.tio.common.ChannelContext;
import org.tio.handler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io
 */
public class SendTaskQueue extends AbstractTaskQueue<ByteBuffer> {

    private ChannelContext context;

    public SendTaskQueue(ChannelContext context) {
        this.context = context;
    }

    @Override
    public void runTask(ByteBuffer byteBuffer) {
        AsynchronousSocketChannel channel = context.getAsynchronousSocketChannel();
        channel.write(byteBuffer, new Object(), new WriteCompletionHandler());
    }
}
