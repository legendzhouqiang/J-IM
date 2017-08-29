package org.tio.common.packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/24
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 默认心跳包
 */
public class HeartBeatPacket implements SuperPacket {
    private byte type = SuperPacket.HEART_MESSAGE_REQ;
    private byte reserved;
    private byte optLen = 4;
    private short bodyLen = 0;
    private byte checkSum;
    private byte[] optData = new byte[4];

    HeartBeatPacket reset(int timeout) {
//        timeout
        return this;
    }



}
