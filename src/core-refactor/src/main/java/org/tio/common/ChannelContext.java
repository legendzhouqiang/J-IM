package org.tio.common;

import lombok.Data;
import org.tio.common.etc.TioLinkListener;
import org.tio.runnable.DecodeTaskQueue;
import org.tio.runnable.EncodeTaskQueue;
import org.tio.runnable.HandlerTaskQueue;
import org.tio.runnable.SendTaskQueue;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 通信管道上下文超类
 */
@Data
public abstract class ChannelContext {

    private final static AtomicInteger idGenerator = new AtomicInteger();

    protected DecodeTaskQueue decodeRunnable = null;

    protected HandlerTaskQueue handlerRunnable = null;

    protected SendTaskQueue sendRunnable = null;

    protected EncodeTaskQueue encodeRunnable = null;

    protected boolean use_checksum = false;

    protected CoreConstant.ClientStatus status;

    protected ChannelStat stat = new ChannelStat();

    protected String ip = null;

    protected Integer port = null;

    protected AsynchronousChannelGroup channelGroup;

    protected TioLinkListener linkListener;

    protected int id = idGenerator.incrementAndGet();

    public Integer id() {
        return id;
    }

    private final ConcurrentHashMap<String, Object> attributeContext = new ConcurrentHashMap<>();

    public Object getAttribute(String name) {
        return attributeContext.get(name);
    }

    public Object setAttribute(String name, Object value) {
        return attributeContext.put(name, value);
    }

    public void removeAttribute(String name) {
        attributeContext.remove(name);
    }

    public List<String> getAttributeNames(){
        return new ArrayList<>(attributeContext.keySet());
    }

    void invalidate(){
        this.status = CoreConstant.ClientStatus.invalid;
    }
}
