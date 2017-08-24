package org.tio.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 线程池-> {@link org.tio.runnable.SynRunnable}
 */
@Slf4j
public class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor {


    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime   unit: second
     * @param runnableQueue
     * @param threadFactory
     * @param name
     * @author: tanyaowu
     */
    public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                              BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory);
    }

    @Override
    public void execute(Runnable runnable) {
        super.execute(runnable);
    }


    @Override
    public <R> Future<R> submit(Runnable runnable, R result) {
        return super.submit(runnable, result);
    }

}