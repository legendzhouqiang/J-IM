package org.tio.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/28
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class ChannelContainer {

    private static ConcurrentHashMap<Node, ChannelContextImpl> channelCache = new ConcurrentHashMap<>();

    public static ChannelContextImpl getChannelContext(Node node) {
        return channelCache.get(node);
    }

    public static ChannelContextImpl getChannelContext(String ip, int port) {
        return getChannelContext(new Node(ip, port));
    }

    public static void putChannelContext(Node node, ChannelContextImpl channelContextImpl) {
        channelCache.put(node, channelContextImpl);
    }

    public static void putChannleContext(String ip, int port, ChannelContextImpl channelContextImpl) {
        putChannelContext(new Node(ip, port), channelContextImpl);
    }
}
