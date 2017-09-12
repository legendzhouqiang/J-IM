package org.tio.common;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/12
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface Channel {

    Integer id();

    Object getAttribute(String name);

    Object setAttribute(String name, Object value);

    void removeAttribute(String name);

    List<String> getAttributeNames();

    void invalidate();

    void bind(AsynchronousSocketChannel channel);

}
