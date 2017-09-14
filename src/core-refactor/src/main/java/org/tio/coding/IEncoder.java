package org.tio.coding;

import org.tio.common.packet.ReadPacket;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface IEncoder {

    ByteBuffer encode(ReadPacket packet);
}
