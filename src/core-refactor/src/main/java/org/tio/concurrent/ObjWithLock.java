package org.tio.concurrent;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/16
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface ObjWithLock<T> {


    /**
     * Gets the lock.
     */
    ReadWriteLock getLock();

    /**
     * Gets the obj.
     */
    T getObj();

    /**
     * Sets the obj.
     */
    void setObj(T obj);

}
