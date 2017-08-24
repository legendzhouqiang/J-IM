package org.tio.handler;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Data
public class ReadCompletionHandler implements CompletionHandler<Integer, Object> {

    @Override
    public void completed(Integer result, Object attachment) {
        if (result > 0) {

        } else {

        }
        attachment.notify();
    }

    @Override
    public void failed(Throwable exc, Object attachment) {

        attachment.notify();
    }
}
