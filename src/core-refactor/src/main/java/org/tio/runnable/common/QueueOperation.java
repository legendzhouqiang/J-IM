package org.tio.runnable.common;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface QueueOperation<T> {
    /**
     * 加入任务队列
     *
     * @param t
     * @return
     */
    public boolean addMsg(T t);

    /**
     * 任务队列是否为空
     *
     * @return
     */
    public boolean hasRemaining();

    /**
     * 清空处理的队列消息
     */
    public void clearMsgQueue();
}
