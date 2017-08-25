package org.tio.runnable;

import org.tio.concurrent.MapWithLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class TaskQueuePool<T extends AbstractTaskQueue> {

    private MapWithLock<T> name4TaskQueueMap = new MapWithLock<>(new HashMap<String, T>());

    private TaskQueuePool() {
    }

    public static <T extends AbstractTaskQueue> TaskQueuePool<T> newInstance() {
        return new TaskQueuePool<>();
    }

    public TaskQueuePool<T> putIntoPool(String name, T t) {
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            name4TaskQueueMap.getObj().put(name, t);
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    public TaskQueuePool<T> escapeFromPool(String name) {
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            name4TaskQueueMap.getObj().remove(name);
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    public void close() {
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            Map<String, T> map = name4TaskQueueMap.getObj();
            for (T t : map.values()) {
                t.setCanceled(true);
            }
        } finally {
            readLock.unlock();
        }
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            name4TaskQueueMap.getObj().clear();
        } finally {
            writeLock.unlock();
        }
    }
}
