package org.tio.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.*;
import org.tio.common.handler.ConnectionCompletionHandler;
import org.tio.runnable.DecodeTaskQueue;
import org.tio.runnable.EncodeTaskQueue;
import org.tio.runnable.HandlerTaskQueue;
import org.tio.runnable.SendTaskQueue;
import org.tio.util.StringUtil;

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
@Data
public class ClientChannelContext extends ChannelContextImpl {

    protected String ip;

    protected Integer port;

    private ClientGroupContext groupContext;

    private AsynchronousSocketChannel asynchronousSocketChannel;

    private Node remote;

    public ClientChannelContext() throws IOException {
    }

    public ClientChannelContext(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
        this.bind(asynchronousSocketChannel);
    }

    public ClientChannelContext bind(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
        this.asynchronousSocketChannel = asynchronousSocketChannel;
        InetSocketAddress remoteAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
        log.info("sock bind local address [{}:{}].", remoteAddress.getHostName(), remoteAddress.getPort());
        remote = new Node(remoteAddress.getHostName(), remoteAddress.getPort());
        return this;
    }

    private ClientChannelContext connect() throws IOException {
        channelGroup = AsynchronousChannelGroup.withThreadPool(groupContext.getAioExecutor());
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
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, CoreConstant.default_receive_buf_size);
        asynchronousSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, CoreConstant.default_send_buf_size);
        if (ip != null && port != null) {
            if (StringUtil.isIp(getIp()) && port > 0) {
                asynchronousSocketChannel.bind(new InetSocketAddress(ip, port));
                log.info("sock bind local address [{}:{}].", ip, port);
            }
            asynchronousSocketChannel.bind(new InetSocketAddress(0));
            InetSocketAddress localAddress = (InetSocketAddress) asynchronousSocketChannel.getLocalAddress();
            log.info("sock bind local address [{}:{}].", localAddress.getHostName(), localAddress.getPort());
        }
        if (remote != null) {
            if (remote.getIp() != null && remote.getPort() != null) {
                InetSocketAddress remoteSocketAddress = new InetSocketAddress(remote.getIp(), remote.getPort());
                asynchronousSocketChannel.connect(remoteSocketAddress, remoteSocketAddress, new ConnectionCompletionHandler());

            }
        }
        return this;
    }
}
