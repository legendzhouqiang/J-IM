package org.tio.client;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.ChannelContext;
import org.tio.common.CoreConstant;
import org.tio.common.Node;
import org.tio.common.SystemTimer;
import org.tio.util.StringUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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

    private Node server;


    public ClientChannelContext() throws IOException {
        channelGroup = AsynchronousChannelGroup.withThreadPool(groupContext.getGroupExecutor());
        long start = SystemTimer.currentTimeMillis();
        this.asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
        long end = SystemTimer.currentTimeMillis();
        long interval = end - start;
        if (interval > 100) {
            log.warn("open socket time out, spend time:{} ms", interval);
        }

        asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, CoreConstant.default_tcp_no_delay);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, CoreConstant.default_reuse_addr);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, CoreConstant.default_keep_alive);

        if (ip != null && port != null) {
            if (StringUtil.isIp(getIp()) && port > 0) {
                asynchronousSocketChannel.bind(new InetSocketAddress(ip, port));
                log.info("sock bind local address [{}:{}].", ip, port);
            }
            asynchronousSocketChannel.bind(new InetSocketAddress(0));
            SocketAddress localAddress = asynchronousSocketChannel.getLocalAddress();
            log.info("sock bind local address port [] port{}", port);
        }
        if (server != null) {
            if (server.getIp() != null) {

            }

        }


    }
}
