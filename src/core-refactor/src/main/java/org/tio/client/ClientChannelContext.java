package org.tio.client;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.ChannelContext;
import org.tio.common.CoreConstant;
import org.tio.common.SystemTimer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 客服端
 */
@Slf4j
public class ClientChannelContext extends ChannelContext {

    private AsynchronousSocketChannel asynchronousSocketChannel;

    public ClientChannelContext() throws IOException {
        channelGroup = AsynchronousChannelGroup.withThreadPool(groupContext.getGroupExecutor());
        long start = SystemTimer.currentTimeMillis();
        this.asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
        ;
        long end = SystemTimer.currentTimeMillis();
        long interval = end - start;
        if (interval > 100) {
            log.warn("open socket time out, spend time:{} ms", interval);
        }

        asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, CoreConstant.default_tcp_no_delay);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, CoreConstant.default_reuse_addr);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, CoreConstant.default_keep_alive);

        InetSocketAddress addr;

        if (ip != null && port != null) {
            if () {

            }
        }

    }
}
