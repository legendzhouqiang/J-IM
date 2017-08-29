package org.tio.common.packet;

import java.io.Serializable;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包接口定义
 */
public interface SuperPacket extends Serializable {

    /** 包头魔数 */
    short magic = -21829; //0xAABB complement number

    /** 包头总长 */
    byte headerSize = 7;

    /** 心跳消息RSP */
    byte HEART_MESSAGE_RSP = 0x01;

    /** 心跳消息REQ */
    byte HEART_MESSAGE_REQ = (byte) (HEART_MESSAGE_RSP | 0x80);

    /** 鉴权响应 */
    byte LOGIN_AUTH_RSP = 0x2;

    /** 鉴权请求 */
    byte LOGIN_AUTH_REQ = (byte) (LOGIN_AUTH_RSP | 0x80);

    /** 断连响应 */
    byte DIS_CONNECT_RSP = 0x3;

    /** 断连请求 */
    byte DIS_CONNECT_REQ = (byte) (DIS_CONNECT_RSP | 0x80);

    /** 探测消息RSP */
    byte DETECT_MESSAGE_RSP = 0x4;

    /** 探测消息REQ */
    byte DETECT_MESSAGE_REQ = (byte) (DETECT_MESSAGE_RSP | 0x80);

    /** 无效消息响应 */
    byte INVALID_MESSAGE_RSP = 0x5;

    /** 无效消息请求 */
    byte INVALID_MESSAGE_REQ = (byte) (INVALID_MESSAGE_RSP | 0x80);

    /** 集群业务响应消息 */
    byte CLUSTER_MESSAGE_RSP = 0x06;

    /** 集群业务请求消息 */
    byte CLUSTER_MESSAGE_REQ = (byte) (CLUSTER_MESSAGE_RSP | 0x80);

    /** 字节流响应消息 */
    byte BYTE_ARRAY_MESSAGE_RSP = 0x07;

    /** 字节流请求消息 */
    byte BYTE_ARRAY_MESSAGE_REQ = (byte) (BYTE_ARRAY_MESSAGE_RSP | 0x80);

    /** 远程接口响应消息 */
    byte REMOTE_INTERFACE_MESSAGE_RSP = 0x08;

    /** 远程接口请求消息 */
    byte REMOTE_INTERFACE_MESSAGE_REQ = (byte) (REMOTE_INTERFACE_MESSAGE_RSP | 0x80);

    /** 安全通信响应消息 */
    byte SECURE_SOCKET_MESSAGE_RSP = 0x09;

    /** 安全通信请求消息 */
    byte SECURE_SOCKET_MESSAGE_REQ = (byte) (SECURE_SOCKET_MESSAGE_RSP | 0x80);
}
