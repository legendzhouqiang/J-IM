package org.tio.common;

import lombok.Data;
import org.tio.runnable.DecodeTaskQueue;
import org.tio.runnable.EncodeTaskQueue;
import org.tio.runnable.HandlerTaskQueue;
import org.tio.runnable.SendTaskQueue;

import java.nio.channels.AsynchronousChannelGroup;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 通信管道上下文超类
 */
@Data
public abstract class ChannelContext {

    protected DecodeTaskQueue decodeRunnable = null;

    protected HandlerTaskQueue handlerRunnable = null;

    protected SendTaskQueue sendRunnable = null;

    protected EncodeTaskQueue encodeRunnable = null;

    protected boolean use_checksum = false;

    protected boolean waitingClose = false;

    protected boolean isClosed = false;

    protected boolean canRemove = false;

    protected boolean isRemoved = false;

    protected ChannelStat stat = new ChannelStat();

    protected String ip = null;

    protected Integer port = null;

    protected GroupContext groupContext;

    protected AsynchronousChannelGroup channelGroup;
}
