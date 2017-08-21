package org.tio.runnable;

import org.tio.common.ChannelContext;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class EncodeTaskQueue<T> extends AbstractTaskQueue<T> {

    private ChannelContext context;

    public EncodeTaskQueue(ChannelContext context) {
        this.context = context;
    }

    @Override
    public void runTask(T t) {

    }
}
