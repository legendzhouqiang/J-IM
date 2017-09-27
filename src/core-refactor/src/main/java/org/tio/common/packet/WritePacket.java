package org.tio.common.packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包接口定义
 */
public interface WritePacket extends SuperPacket {

    /** 第一位:1 reply :消息是否需要返回 0:不需要;1:需要,剩余7位包类型
     *  0:保留;1:心跳包;2心跳相应包;
     */
    WritePacket setPacketType(byte packetType);

    /** ack */
    WritePacket setNeedReplay();

    /** 保留字段 */
    WritePacket setReserved(byte reserved);

    /** 可选区长度 */
    WritePacket setOptionalLength(byte optionalLength);

    /** 数据区长度 */
    WritePacket setBodyLength(short bodyLength);



    /** 可选区数据 */
    WritePacket setOptional(byte[] optional);

    /** 包序列 */
    WritePacket setPacketSeq(short count);

    /** 数据区数据 */
    WritePacket setBody(byte[] body);
}
