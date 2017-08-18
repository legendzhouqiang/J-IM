package org.tio.common;

import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 核心常量类
 */
public interface CoreConstant {

    /** 网络字节流对齐方式 */
    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /** 心跳超时时间(单位: 毫秒)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数 */
    long defaultHeartBeatTimeout = 1000 * 120;

    /** 默认最大消息队列长度，超出会打印警告日志 */
    long defaultMaxMsgQueueSize = 10000;

    /** unknown ip for close connection */
    String unknown_address_ip = "$UNKNOWN";

    /** unknown_address_ip_seq for close connection */
    AtomicInteger unknown_address_port_seq = new AtomicInteger();
}
