package org.tio.runnable;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;
import org.tio.common.misc.TioException;
import org.tio.concurrent.MapWithLock;
import org.tio.robin.*;
import org.tio.runnable.common.AbstractTaskQueue;

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
@Slf4j
public class TaskQueuePool<T extends AbstractTaskQueue> {

    private MapWithLock<T> name4TaskQueueMap = new MapWithLock<>(new HashMap<String, T>());

    private CoreConstant.RoundRobin roundRobin = CoreConstant.default_robin_type;

    private IRobin robin;

    private String name;

    private TaskQueuePool() {
    }

    public static <T extends AbstractTaskQueue> TaskQueuePool<T> newInstance(String name) throws TioException {
        if (name == null) {
            log.warn("Can not set TaskQueuePool name to be null.");
            throw new TioException(TioException.ExceptionCodeEnum.Null_Input_Error.code);
        }
        TaskQueuePool<T> taskQueuePool = new TaskQueuePool<>();
        taskQueuePool.name = name;
        if (taskQueuePool.roundRobin.equals(CoreConstant.RoundRobin.weight_round_robin)) {
            taskQueuePool.robin = WeightRoundRobin.newInstance(taskQueuePool.name);
        } else {
            taskQueuePool.robin = RoundRobin.newInstance(taskQueuePool.name);
        }
        return taskQueuePool;
    }

    public TaskQueuePool<T> putIntoPool(String name, T t, int weight) throws TioException {
        if (roundRobin.equals(CoreConstant.RoundRobin.weight_round_robin) && robin instanceof IWeightRoundRobin) {
            ReadWriteLock lock = name4TaskQueueMap.getLock();
            Lock writeLock = lock.writeLock();
            try {
                writeLock.lock();
                name4TaskQueueMap.getObj().put(name, t);
                IWeightRoundRobin robin = (IWeightRoundRobin) this.robin;
                robin.expand(name, weight);
            } finally {
                writeLock.unlock();
            }
            return this;
        } else {
            throw new TioException(TioException.ExceptionCodeEnum.Illegal_Operation_Error.code);
        }]
    }

    public TaskQueuePool<T> putIntoPool(String name, T t) throws TioException {
        if (roundRobin.equals(CoreConstant.RoundRobin.round_robin) && robin instanceof IRoundRobin) {
            ReadWriteLock lock = name4TaskQueueMap.getLock();
            Lock writeLock = lock.writeLock();
            try {
                writeLock.lock();
                name4TaskQueueMap.getObj().put(name, t);
                IRoundRobin robin = (IRoundRobin) this.robin;
                robin.expand(name);
            } finally {
                writeLock.unlock();
            }
            return this;
        } else {
            throw new TioException(TioException.ExceptionCodeEnum.Illegal_Operation_Error.code);
        }
    }

    public TaskQueuePool<T> escapeFromPool(String name) {
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            if (roundRobin.equals(CoreConstant.RoundRobin.weight_round_robin)) {
                IWeightRoundRobin robin = (IWeightRoundRobin) this.robin;
                robin.shrink(name);
            } else {
                IRoundRobin robin = (IRoundRobin) this.robin;
                robin.shrink(name);
            }
            name4TaskQueueMap.getObj().remove(name);
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    public T getTaskQueue() {
        String round = robin.round();
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return name4TaskQueueMap.getObj().get(round);
        } finally {
            readLock.unlock();
        }
    }

    public void start() {

    }

    public void stop() {
        ReadWriteLock lock = name4TaskQueueMap.getLock();
        Lock readLock = lock.writeLock();
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
