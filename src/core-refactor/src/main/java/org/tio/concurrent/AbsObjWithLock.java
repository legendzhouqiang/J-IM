package org.tio.concurrent;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/16
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class AbsObjWithLock<T> implements ObjWithLock<T> {

    protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected T obj;

    public ReadWriteLock getLock() {
        return lock;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
