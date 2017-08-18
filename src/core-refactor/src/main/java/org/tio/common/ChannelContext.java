package org.tio.common;

import lombok.Data;
import org.tio.runnable.SynRunnable;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 通信管道上下文超类
 */
@Data
public abstract class ChannelContext {

    protected SynRunnable decodeRunnable = null;

    protected SynRunnable handlerRunnable = null;

    protected SynRunnable sendRunnable = null;

    protected SynRunnable encodeRunnable = null;

    protected boolean waitingClose = false;

    protected boolean isClosed = false;

    protected boolean canRemove = false;

    protected boolean isRemoved = false;

    protected ChannelStat stat = new ChannelStat();
}
