package org.tio.runnable;

import org.tio.concurrent.ListWithLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class RoundRobin {

    private ListWithLock<String> arr;

    private int currentIndex;
    private int total;

    private RoundRobin() {
        ReadWriteLock lock = arr.getLock();
        Lock readLock = lock.readLock();
        try {
            total = arr.getObj().size();
            currentIndex = total - 1;
        } finally {
            readLock.unlock();
        }
    }

    public static RoundRobin newInstance() {
        return new RoundRobin();
    }

    private RoundRobin expand(String str) {
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

    private RoundRobin shrink(String str) {
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

}
