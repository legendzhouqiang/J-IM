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

    /** 默认网络字节流对齐方式 */
    ByteOrder default_byte_order = ByteOrder.BIG_ENDIAN;

    /** 默认心跳超时时间(单位:毫秒)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数 */
    long default_heart_beat_timeout = 1000 * 120;

    /** 默认服务器/客服端时间矫正间隔，即每n次心跳回复包中会有一次返回服务器时间，用于时间校准 */
    int default_timestamp_rectify_interval = 20;

    /** 默认关闭客服端时间校准 */
    boolean default_client_timestamp_rectify = false;

    /** 默认消息队列容量 */
    int default_msg_queue_capacity = 1500;

    /** 默认最大消息队列长度，超出会打印警告日志 */
    long default_msg_queue_max_size = 1000;

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

    /** 默认任务队列默认轮询算法 */
    RoundRobin default_robin_type = RoundRobin.round_robin;

    enum RoundRobin{
        round_robin,weight_round_robin;
    }

    enum ConnectionStatus {
        /** 初始状态 */
        Init,
        /** 开始建立连接 */
        Connecting,
        /** 建立 */
        establish,
        /** 开始关闭 */
        closing,
        /** 已经关闭 */
        closed,
        /** 失效 */
        invalid;
    }

    enum Status {
        /** 初始状态 */
        Init,
        /** 启动中 */
        STARTING,
        /** 运行中 */
        RUNING,
        /** 停止中 */
        STOPPING,
        /** 已停止 */
        STOPPED,
        /** 启动/运行异常 */
        Abnormal
    }

}

enum StatisticStatus {
    OPEN(true), Close(false);

    boolean status;

    StatisticStatus(boolean status) {
        this.status = status;
    }
}

