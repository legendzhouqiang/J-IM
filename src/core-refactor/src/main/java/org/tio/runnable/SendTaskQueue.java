package org.tio.runnable;

import org.tio.client.ClientChannelContext;
import org.tio.handler.WriteCompletionHandler;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io发送器
 */
public class SendTaskQueue extends AbstractTaskQueue<ByteBuffer> {

    private ClientChannelContext clientContext;

    public SendTaskQueue(ClientChannelContext context) {
        this.clientContext = context;
    }

    @Override
    public void runTask(ByteBuffer byteBuffer) {
        AsynchronousSocketChannel channel = clientContext.getAsynchronousSocketChannel();
        channel.write(byteBuffer, new Object(), new WriteCompletionHandler());
    }
}
