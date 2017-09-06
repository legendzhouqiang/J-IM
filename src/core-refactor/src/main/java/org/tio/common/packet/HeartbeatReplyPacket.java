package org.tio.common.packet;

import org.tio.util.ByteUtil;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/29
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 心跳消息RSP包
 */
public class HeartbeatReplyPacket extends AbstractPacket {

    public HeartbeatReplyPacket() {
        super.type = SuperPacket.HEART_MESSAGE_RSP;

    }

    public HeartbeatReplyPacket(long currentTimeMillis) {
        this();
        filling(currentTimeMillis);
    }

    public void filling(long currentTimeMillis) {
        super.optLen = 8;
        super.optData = ByteUtil.longToByteArray(currentTimeMillis);
    }
}
