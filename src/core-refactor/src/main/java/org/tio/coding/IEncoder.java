package org.tio.coding;

import java.nio.ByteBuffer;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface IEncoder<packet> {

    ByteBuffer encode(packet packet);
}