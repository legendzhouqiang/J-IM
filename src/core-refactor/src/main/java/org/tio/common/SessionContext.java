package org.tio.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/28
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface SessionContext {

    ConcurrentHashMap<Integer, Session> sessionCache = new ConcurrentHashMap<>();

    Session getSession(String id);
}
