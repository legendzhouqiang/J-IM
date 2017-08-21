package org.tio.runnable;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/20
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class DecodeTaskQueue extends AbstractSynTaskQueue<ByteBuffer> {

    private volatile boolean isCancel = false;

    private ReentrantLock lock = new ReentrantLock();

    private String name;

    @Override
    public ReentrantLock runningLock() {
        return lock;
    }

    @Override
    public boolean isCanceled() {
        return isCancel;
    }

    @Override
    public void setCanceled(boolean cancelFlag) {
        this.isCancel = cancelFlag
    }

    @Override
    public void runTask() {

    }

    @Override
    public String getName() {
        return name;
    }


}
