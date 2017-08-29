package org.tio.common;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/28
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface Session {

    AtomicInteger idGenerator = new AtomicInteger();

    Integer id();

    Object getAttribute(String name);

    Object setAttribute(String name,Object value);

    void removeAttribute(String name);

    List<String> getAttributeNames();

    SessionContext getSessionContext();

    void invalidate();
}
