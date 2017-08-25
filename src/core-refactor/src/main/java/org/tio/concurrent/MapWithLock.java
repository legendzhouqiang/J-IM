package org.tio.concurrent;

import java.util.Map;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/25
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class MapWithLock<T> extends AbsObjWithLock<Map<String,T>> {

    public MapWithLock(Map<String,T> obj) {
        super.obj = obj;
    }
}
