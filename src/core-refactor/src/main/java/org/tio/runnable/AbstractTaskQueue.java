package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/21
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public abstract class AbstractTaskQueue<T> extends AbstractSynTaskQueue<T> {

    private boolean isCancel = false;

    protected String name;

    @Override
    public boolean isCanceled() {
        return isCancel;
    }

    @Override
    public void setCanceled(boolean cancelFlag) {
        this.isCancel = cancelFlag;
        if (cancelFlag) {
            Thread.currentThread().interrupt();
            log.info("TaskQueue[{}] has been interrupted, and left {} message has not been consumed.", getName(), msgQueue.size());
            msgQueue.clear();
            log.info("TaskQueue[{}]'s msgQueue has been clean up now.");
        }
    }

    @Override
    public String getName() {
        return name;
    }

}
