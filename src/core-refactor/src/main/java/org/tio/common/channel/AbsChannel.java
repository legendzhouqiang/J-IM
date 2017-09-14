package org.tio.common.channel;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;
import org.tio.common.Node;
import org.tio.common.TioParams;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public abstract class AbsChannel implements Channel {

    private final static AtomicInteger idGenerator = new AtomicInteger();

    // 不采取volatile方式变量,允许小范围进程间不可见
    protected boolean use_checksum = Boolean.getBoolean(TioParams.default_params_use_checksum);

    private final Map<String, Object> attributeContext = Maps.newConcurrentMap();

    private int id = idGenerator.incrementAndGet();

    private CoreConstant.ConnectionStatus status = CoreConstant.ConnectionStatus.Init;

    protected ChannelStat stat = new ChannelStat();

//    protected TioLinkListener linkListener;
//    protected TioIntercepter intercepter;
    // TODO: 2017/9/14 需要移动到channelcontext

    private Node remoteNode;

    private Node localNode;

    protected AsynchronousSocketChannel channel;

    @Override
    public CoreConstant.ConnectionStatus status() {
        return status;
    }

    @Override
    public void changeStatus(CoreConstant.ConnectionStatus status) {
        this.status = status;
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Object getAttribute(String name) {
        return attributeContext.get(name);
    }

    @Override
    public Object setAttribute(String name, Object value) {
        return attributeContext.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributeContext.remove(name);
    }

    @Override
    public List<String> getAttributeNames() {
        return new ArrayList<>(attributeContext.keySet());
    }

    @Override
    public void invalidate() {
        this.status = CoreConstant.ConnectionStatus.invalid;
    }

    @Override
    public void bind(AsynchronousSocketChannel channel) throws IOException {
        InetSocketAddress localAddress = (InetSocketAddress) channel.getLocalAddress();
        localNode = new Node(localAddress.getHostName(), localAddress.getPort());
        InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        remoteNode = new Node(remoteAddress.getHostName(), remoteAddress.getPort());
        log.info("Set up a new connection ==> [ {} to {} ]", localNode, remoteNode);
        this.channel = channel;
    }

    @Override
    public ChannelStat stat() {
        return this.stat;
    }

    @Override
    public boolean useChecksum() {
        return use_checksum;
    }
}
