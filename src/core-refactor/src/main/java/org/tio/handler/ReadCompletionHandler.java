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
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if (result > 0) {

        } else {

        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }
}
