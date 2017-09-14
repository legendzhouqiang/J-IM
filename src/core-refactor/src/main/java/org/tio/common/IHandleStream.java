package org.tio.common;


import org.tio.common.packet.ReadPacket;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface IHandleStream {

    void handlePacket(ReadPacket packet);
}
