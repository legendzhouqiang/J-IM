package org.tio.common.channel;

import org.tio.common.packet.ReadPacket;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/13
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface MessageHandler {

    void asynHandleMessage(ReadPacket packet);

    <T> T synHandleMessage(ReadPacket packet);
}
