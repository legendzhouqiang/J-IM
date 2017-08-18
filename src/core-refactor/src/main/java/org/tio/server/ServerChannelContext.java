package org.tio.server;

import org.tio.common.ChannelContext;
import org.tio.common.Node;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class ServerChannelContext extends ChannelContext{

    private AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    private ConcurrentHashMap<Node,Void> clientNodes = new ConcurrentHashMap<>();

    private Node serverNode = null;

}
