package org.tio.runnable;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public abstract class AbstractTaskQueue<T> extends AbstractSynTaskQueue<T> {

    private boolean isCancel = false;

    private ReentrantLock lock = new ReentrantLock();

    protected String name;

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
        this.isCancel = cancelFlag;
        if (cancelFlag) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
