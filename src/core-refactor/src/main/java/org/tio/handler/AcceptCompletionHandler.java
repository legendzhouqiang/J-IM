package org.tio.handler;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.channel.Channel;
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
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Channel> {


    @Override
    public void completed(AsynchronousSocketChannel channel, Channel context) {
        try {
            context.bind(channel);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, CoreConstant.default_reuse_addr);
            channel.setOption(StandardSocketOptions.SO_KEEPALIVE, CoreConstant.default_keep_alive);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, CoreConstant.default_receive_buf_size);
            channel.setOption(StandardSocketOptions.SO_SNDBUF, CoreConstant.default_send_buf_size);
            ByteBuffer buffer = ByteBuffer.allocateDirect(CoreConstant.default_receive_buf_size);
            buffer.order(CoreConstant.default_byte_order);
            while (true) {
                if (CoreConstant.ConnectionStatus.closing.equals(context.status())
                        || CoreConstant.ConnectionStatus.closed.equals(context.status())
                        || CoreConstant.ConnectionStatus.invalid.equals(context.status())) {
                    break;
                }
                channel.read(buffer, context, new ReadCompletionHandler());
//                context.getDecodeRunnable().addMsg(buffer.slice());
                buffer.clear();
            }
        } catch (IOException e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
    }

    @Override
    public void failed(Throwable exc, Channel context) {

    }
}
