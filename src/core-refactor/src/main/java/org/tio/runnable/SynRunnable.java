package org.tio.runnable;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 同步运行任务接口
 */
public interface SynRunnable extends Runnable {

    /**
     * 获取同步任务锁
     * @return
     */
    ReentrantLock runningLock();

    /**
     * 是否被取消
     * @return
     */
    boolean isCanceled();

    /**
     * 设置任务取消
     * @param cancelFlag
     */
    void setCanceled(boolean cancelFlag);

    /**
     * 实质任务payload
     */
    void runTask();

    /**
     * 任务队列名称
     *
     * @return
     */
    abstract String getName();
}
