package org.tio.common.etc;

import org.tio.common.ChannelContextImpl;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/6
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io 拦截器
 */
public interface TioIntercepter {

    /**
     * 处理方法之前调用
     * @param context
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself.
     * @throws TioException
     */
    boolean preHandle(ChannelContextImpl context) throws TioException;

    /**
     * 处理方法之后调用
     * @param context
     * @throws TioException
     */
    void postHandle(ChannelContextImpl context) throws TioException;

    /**
     * 处理发生异常
     * @param context
     * @param e
     * @throws TioException
     */
    void abort(ChannelContextImpl context, TioException e) throws TioException;
}
