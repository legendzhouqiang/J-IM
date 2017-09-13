package org.tio.common.misc;


import org.tio.common.channel.Channel;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/6
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io 拦截器
 */
public interface TioIntercepter {

    /**
     * 处理方法之前调用
     * @param channel
     * @return {@code true} if the execution chain should proceed with the
     * next interceptor or the handler itself.
     * @throws TioException
     */
    boolean preHandle(Channel channel) throws TioException;

    /**
     * 处理方法之后调用
     * @param channel
     * @throws TioException
     */
    void postHandle(Channel channel) throws TioException;

    /**
     * 处理发生异常
     * @param channel
     * @param e
     * @throws TioException
     */
    void abort(Channel channel, TioException e);
}
