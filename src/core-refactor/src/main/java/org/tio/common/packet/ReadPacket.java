package org.tio.common.packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包接口定义
 */
public interface ReadPacket extends SuperPacket {

    /** 第一位:1 reply :消息是否需要返回 0:不需要;1:需要,剩余7位包类型
     *  0:保留;1:心跳包;2心跳相应包;
     */
    byte packetType();

    boolean needReply();

    /** 保留字段 */
    byte reserved();

    /** 可选区长度 */
    byte optionalLength();

    /** 数据区长度 */
    short bodyLength();

    /** 数据校验 */
    byte checkSum();

    /** 数据包头 */
    byte[] header();

    /** 可选区数据 */
    byte[] optional();

    /** 包序列 */
    short packetSeq();

    /** 数据区数据 */
    byte[] body();
}
