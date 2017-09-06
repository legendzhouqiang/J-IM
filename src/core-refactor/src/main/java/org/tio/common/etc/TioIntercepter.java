package org.tio.common.etc;

import org.tio.common.ChannelContext;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/6
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface TioIntercepter {

    boolean preHandle(ChannelContext context) throws TioException;

    void postHandle(ChannelContext context) throws TioException;

    void abort(ChannelContext context, TioException e) throws TioException;
}
