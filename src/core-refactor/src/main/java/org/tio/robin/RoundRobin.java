package org.tio.robin;

import lombok.extern.slf4j.Slf4j;
import org.tio.common.misc.TioException;
import org.tio.concurrent.ListWithLock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 轮询算法
 */
@Slf4j
public class RoundRobin implements IRoundRobin {

    private ListWithLock<String> arr;

    private static Map<String, RoundRobin> cache = new HashMap<>();

    private int currentIndex;
    private int total;

    private RoundRobin() {
        arr = new ListWithLock<>(new ArrayList<String>());
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            total = arr.getObj().size();
            currentIndex = total - 1;
        } finally {
            readLock.unlock();
        }
    }

    public static RoundRobin newInstance(String cacheKey) throws TioException {
        if (cacheKey == null) {
            log.warn("Can not input null cacheKey for Round IRobin Cache.");
            throw new TioException(TioException.ExceptionCodeEnum.Null_Input_Error.code);
        }
        RoundRobin roundRobin = cache.get(cacheKey);
        if (roundRobin == null) {
            roundRobin = new RoundRobin();
            cache.put(cacheKey, roundRobin);
        }
        return roundRobin;
    }

    @Override
    public IRoundRobin expand(String str) {
        ReadWriteLock lock = arr.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            arr.getObj().add(str);
            total = arr.getObj().size();
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    @Override
    public IRoundRobin shrink(String str) {
        ReadWriteLock lock = arr.getLock();
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            arr.getObj().remove(str);
            total = arr.getObj().size();
        } finally {
            writeLock.unlock();
        }
        return this;
    }

    @Override
    public String round() {
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            currentIndex = (currentIndex + 1) % total;
            return arr.getObj().get(currentIndex);
        } finally {
            readLock.unlock();
        }
    }

    public static void main(String[] args) {
        RoundRobin robin = new RoundRobin();
        robin.expand("a").expand("b").expand("c");
        for (int i = 0; i < 20; i++) {
            System.out.println(robin.round());
        }
    }
}
