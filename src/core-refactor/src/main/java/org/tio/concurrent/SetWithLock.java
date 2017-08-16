package org.tio.concurrent;

import java.util.Set;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/16
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class SetWithLock<T> extends AbsObjWithLock<Set<T>>{

    public SetWithLock(Set<T> obj) {
        super.obj = obj;
    }

}
