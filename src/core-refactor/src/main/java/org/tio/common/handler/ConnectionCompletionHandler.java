package org.tio.common.handler;

import java.nio.channels.CompletionHandler;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/23
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public class ConnectionCompletionHandler implements CompletionHandler<Void, Object> {

    @Override
    public void completed(Void result, Object attachment) {

    }

    @Override
    public void failed(Throwable exc, Object attachment) {

    }
}
