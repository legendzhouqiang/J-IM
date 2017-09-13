package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

import static org.tio.common.CoreConstant.default_msg_queue_capacity;
import static org.tio.common.CoreConstant.default_msg_queue_max_size;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Slf4j
public final class ByteBufferQueue implements QueueOperation<ByteBuffer> {

    private String name;

    private LinkedBlockingDeque<ByteBuffer> msgQueue = new LinkedBlockingDeque<>(default_msg_queue_capacity);

    public ByteBufferQueue(String name) {
        this.name = name;
    }

    @Override
    public boolean addMsg(ByteBuffer t) {
        boolean flag = msgQueue.offer(t);
        int size = msgQueue.size();
        if (size > default_msg_queue_max_size) {
            log.warn("ByteBufferQueue[{}] is overflow the defaultMaxSize[{}], and the current size of taskQueue[{}] is {}.",
                    name, default_msg_queue_max_size, name, size);
        }
        return flag;
    }

    @Override
    public boolean hasRemaining() {
        return msgQueue.size() != 0;
    }

    @Override
    public void clearMsgQueue() {
        msgQueue.clear();
    }
}
