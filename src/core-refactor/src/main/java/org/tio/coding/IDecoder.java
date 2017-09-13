package org.tio.coding;

import org.tio.common.packet.ReadPacket;

import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface IDecoder {

    ReadPacket decode(ByteBuffer buffer, LinkedBlockingDeque<ByteBuffer> msgQueue) throws InterruptedException;

}
