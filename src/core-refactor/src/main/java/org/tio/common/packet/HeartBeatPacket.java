package org.tio.common.packet;

import org.tio.util.ByteUtil;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/24
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 心跳消息REQ包
 */
public class HeartBeatPacket extends AbsPacket {

    public HeartBeatPacket reset(int timeout) {
        super.optLen = 4;
        super.optData = ByteUtil.intToByteArray(timeout);
        return this;
    }

    public HeartBeatPacket(int timeout) {
        this();
        this.reset(timeout);
    }

    public HeartBeatPacket() {
        super.type = SuperPacket.HEART_MESSAGE_REQ;
    }
}
