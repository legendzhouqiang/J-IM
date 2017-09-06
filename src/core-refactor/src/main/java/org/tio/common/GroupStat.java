package org.tio.common;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 分组统计实体
 */
@Data
public class GroupStat {

    /** 关闭连接数 */
    private AtomicLong closedCount = new AtomicLong();
    /** 打开连接数 */
    private AtomicLong connectionCount = new AtomicLong();
    /** 接收到消息包数 */
    private AtomicLong receivedPacket = new AtomicLong();
    /** 接收到消息字节数 */
    private AtomicLong receivedBytes = new AtomicLong();
    /** 处理消息包数 */
    private AtomicLong handledPacket = new AtomicLong();
    /** 处理消息包数 */
    private AtomicLong handledBytes = new AtomicLong();
    /** 发送消息包数 */
    private AtomicLong sentPacket = new AtomicLong();
    /** 发送消息字节数 */
    private AtomicLong sentBytes = new AtomicLong();

}
