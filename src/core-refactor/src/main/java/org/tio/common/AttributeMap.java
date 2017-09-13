package org.tio.common;

import java.util.List;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface AttributeMap {

    Object getAttribute(String name);

    Object setAttribute(String name, Object value);

    void removeAttribute(String name);

    List<String> getAttributeNames();
}
