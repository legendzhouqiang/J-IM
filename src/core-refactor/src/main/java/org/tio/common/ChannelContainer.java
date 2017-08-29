package org.tio.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/28
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class ChannelContainer {

    private static ConcurrentHashMap<Node, ChannelContext> channelCache = new ConcurrentHashMap<>();

    public static ChannelContext getChannelContext(Node node) {
        return channelCache.get(node);
    }

    public static ChannelContext getChannelContext(String ip, int port) {
        return getChannelContext(new Node(ip, port));
    }

    public static void putChannelContext(Node node, ChannelContext channelContext) {
        channelCache.put(node, channelContext);
    }

    public static void putChannleContext(String ip, int port, ChannelContext channelContext) {
        putChannelContext(new Node(ip, port), channelContext);
    }
}
