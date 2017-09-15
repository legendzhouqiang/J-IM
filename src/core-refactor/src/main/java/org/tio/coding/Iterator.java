package org.tio.coding;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/15
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface Iterator<T> {

    /**
     * 是否还有下一个
     * @return
     */
    boolean hasNext();

    /**
     * 下一个元素
     * @return
     */
    T next();
}
