package org.tio.common.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 抽象任务队列类
 */
@Slf4j
public abstract class AbstractSynTaskQueue<T> implements SynRunnable {

    /**
     * 任务队列
     */
    private ConcurrentLinkedQueue<T> msgQueue = new ConcurrentLinkedQueue<T>();

    /**
     * 加入任务队列
     *
     * @param t
     * @return
     */
    public boolean addMsg(T t) {
        if (this.isCanceled()) {
            log.debug("taskQueue[{}] has been canceled.", this);
            return false;
        }
        boolean flag = msgQueue.add(t);
        if (msgQueue.size() > CoreConstant.defaultMaxMsgQueueSize) {
            log.warn("");
        }
        return flag;
    }

    /**
     * 任务队列是否为空
     * @return
     */
    public boolean hasRemaining() {
        return msgQueue.size() != 0;
    }

    /**
     * 清空处理的队列消息
     */
    public void clearMsgQueue() {
        msgQueue.clear();
    }
}
