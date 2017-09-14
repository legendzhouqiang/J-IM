package org.tio.runnable.common;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 同步运行任务接口
 */
public interface SynRunnable<T> extends Runnable {

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
    void runTask(T t) throws InterruptedException;

    /**
     * 任务队列名称
     *
     * @return
     */
    String getName();

    /**
     * 绅士关闭线程
     */
    void gentleShutdown();
}
