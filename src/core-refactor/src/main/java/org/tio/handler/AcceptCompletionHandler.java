package org.tio.handler;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 连接处理器
 */
@Slf4j
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

    @Override
    public void completed(AsynchronousSocketChannel channel, Object attachment) {
        try {
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, CoreConstant.default_receive_buf_size);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, CoreConstant.default_send_buf_size);
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            ByteBuffer buffer = ByteBuffer.allocateDirect(CoreConstant.default_receive_buf_size);
            buffer.clear();
            channel.read(buffer, buffer, new ReadCompletionHandler());
        } catch (IOException e) {
            log.error(Throwables.getStackTraceAsString(e));
        }

    }

    @Override
    public void failed(Throwable exc, Object attachment) {

    }
}
