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
    long default_heart_beat_timeout = 1000 * 120;

    /** 默认最大消息队列长度，超出会打印警告日志 */
    long default_max_msg_queue_size = 10000;

    /** 默认交互超时时间(unit:second),需要答复型消息 */
    short default_interactive_timeout = 20;

    /** 默认开启(服务器/客服端)状态监控 */
    boolean default_statistics_status = StatisticStatus.OPEN.status;

    /** unknown ip for close connection */
    String unknown_address_ip = "$UNKNOWN";

    /** unknown_address_ip_seq for close connection */
    AtomicInteger unknown_address_port_seq = new AtomicInteger();

    /** 默认接收buffer的大小 */
    int default_receive_buf_size = 32 * 1024;

    /** 默认发送buffer的大小 */
    int default_send_buf_size = 32 * 1024;

    /** 默认重用地址 */
    boolean default_reuse_addr = true;

    /** 默认开启TCP自己心跳机制保持连接 */
    boolean default_keep_alive = true;

    /** Disable the Nagle algorithm. */
    boolean default_tcp_no_delay = true;
}

enum StatisticStatus {
    OPEN(true), Close(false);

    boolean status;

    StatisticStatus(boolean status) {
        this.status = status;
    }
}
