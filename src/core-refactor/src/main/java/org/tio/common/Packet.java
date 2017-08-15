package org.tio.common;

import java.io.Serializable;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包
 */
public interface Packet extends Serializable {

    short packetCount();

    void setPacketCount();

    byte packetType();

    byte setPacketType(byte packetType);

    byte checkSum();

    byte setCheckSum(byte checkSum);

    byte[] header();

    byte optionalLength();

    byte[] optional();

    short bodyLength();

    byte[] body();
}
