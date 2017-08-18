package org.tio.executor;

import lombok.extern.slf4j.Slf4j;
import org.tio.runnable.SynRunnable;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 线程池-> {@link org.tio.runnable.SynRunnable}
 */
@Slf4j
public class SynThreadPoolExecutor extends ThreadPoolExecutor {


    /**
     * @param corePoolSize
     * @param maximumPoolSize
     * @param keepAliveTime   unit: second
     * @param runnableQueue
     * @param threadFactory
     * @param name
     * @author: tanyaowu
     */
    public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                 BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory);
    }

    private Lock tryLock(Runnable runnable) {
        return null;
    }

    @Override
    public void execute(Runnable runnable) {
        if (runnable instanceof SynRunnable) {
            SynRunnable synRunnable = (SynRunnable) runnable;
            ReentrantLock lock = synRunnable.runningLock();
            boolean b = lock.tryLock();
            try {
                if (b) {
                    super.execute(runnable);
                } else {
                    log.debug(synRunnable.getName());
                }
            } finally {
                if (b) {
                    lock.unlock();
                }
            }
        } else {
            super.execute(runnable);
        }
    }


    @Override
    public <R> Future<R> submit(Runnable runnable, R result) {
        if (runnable instanceof SynRunnable) {
            SynRunnable synRunnable = (SynRunnable) runnable;
            ReentrantLock lock = synRunnable.runningLock();
            boolean b = lock.tryLock();
            try {
                if (b) {
                    return super.submit(runnable, result);
                } else {
                    log.debug(synRunnable.getName());
                    return new FutureTask<>(runnable, result);
                }
            } finally {
                if (b) {
                    lock.unlock();
                }
            }
        } else {
            return super.submit(runnable, result);
        }
    }

}