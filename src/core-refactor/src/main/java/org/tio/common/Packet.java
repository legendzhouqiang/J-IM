package org.tio.common;

import java.io.Serializable;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包接口定义
 */
public interface Packet extends Serializable {

    /** 包头魔数 */
    short magic = -21829; //0xAABB complement number

    /** 包头总长 */
    int headerSize = 7;

    /** 第一位:1 reply :消息是否需要返回 0:不需要;1:需要,剩余7位包类型 */
    byte packetType();

    byte setPacketType(byte packetType);

    /** 保留字段 */
    byte reserved();

    void setReserved(byte reserved);


    /** 可选区长度 */
    byte optionalLength();

    void setOptionalLength(byte optionalLength);

    /** 数据区长度 */
    short bodyLength();

    void setBodyLength(short bodyLength);

    /** 数据校验 */
    byte checkSum();

    byte setCheckSum(byte checkSum);

    /** 数据包头 */
    byte[] header();

    /** 可选区数据 */
    byte[] optional();

    void setOptional(byte[] optional);

    /** 包序列 */
    short packetSeq();

    void setPacketSeq(short count);

    /** 数据区数据 */
    byte[] body();

    void setBody(byte[] body);
}
