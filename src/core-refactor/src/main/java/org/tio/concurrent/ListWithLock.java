package org.tio.concurrent;

import java.util.List;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class ListWithLock<T> extends AbsObjWithLock<List<T>> {

    public ListWithLock(List<T> obj) {
        super.obj = obj;
    }
}
