package org.tio.common.channel;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;
import org.tio.common.Node;
import org.tio.common.SystemTimer;
import org.tio.common.handler.AcceptCompletionHandler;
import org.tio.server.ServerGroupContext;
import org.tio.util.StringUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public class ServerChannel {

    private String ip;

    private Integer port;

    private AsynchronousChannelGroup channelGroup;

    private ServerGroupContext groupContext;

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private ConcurrentHashMap<Integer, Void> clientNodes = new ConcurrentHashMap<>();

    private Node serverNode = null;

    public ServerChannel() throws IOException {
        channelGroup = AsynchronousChannelGroup.withThreadPool(groupContext.getAioExecutor());
        long start = SystemTimer.currentTimeMillis();
        this.asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open(channelGroup);
        asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, CoreConstant.default_reuse_addr);
        asynchronousServerSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, CoreConstant.default_receive_buf_size);

        long end = SystemTimer.currentTimeMillis();
        long interval = end - start;
        if (interval > 100) {
            log.warn("open socket time out, spend time:{} ms", interval);
        }
        if (ip != null && port != null) {
            if (StringUtil.isIp(ip) && port > 0) {
                asynchronousServerSocketChannel.bind(new InetSocketAddress(ip, port));
                log.info("sock bind local address [{}:{}].", ip, port);
            }
            asynchronousServerSocketChannel.bind(new InetSocketAddress(0));
            InetSocketAddress localAddress = (InetSocketAddress) asynchronousServerSocketChannel.getLocalAddress();
            log.info("sock bind local address [{}:{}].", localAddress.getHostName(), localAddress.getPort());
        }

        asynchronousServerSocketChannel.accept(new ChannelContext(),new AcceptCompletionHandler());
    }
}