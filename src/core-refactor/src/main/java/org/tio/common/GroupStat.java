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
    private AtomicLong countOfClosed = new AtomicLong();
    /** 打开连接数 */
    private AtomicLong countOfConnection = new AtomicLong();
    /** 接收到消息包数 */
    private AtomicLong countOfReceivedPacket = new AtomicLong();
    /** 接收到消息字节数 */
    private AtomicLong countOfReceivedBytes = new AtomicLong();
    /** 处理消息包数 */
    private AtomicLong countOfHandledPacket = new AtomicLong();
    /** 处理消息包数 */
    private AtomicLong countOfHandledBytes = new AtomicLong();
    /** 发送消息包数 */
    private AtomicLong countOfSentPacket = new AtomicLong();
    /** 发送消息字节数 */
    private AtomicLong countOfSentBytes = new AtomicLong();

}
