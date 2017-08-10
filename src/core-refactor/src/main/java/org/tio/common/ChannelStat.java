package org.tio.common;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 管道统计实体
 */
@Data
public class ChannelStat {

    /** 最近一次收到业务消息包的时间(一个完整的业务消息包，一部分消息不算) */
	private long latestTimeOfReceivedPacket = SystemTimer.currentTimeMillis();

	/** 最近一次发送业务消息包的时间(一个完整的业务消息包，一部分消息不算) */
	private long latestTimeOfSentPacket = SystemTimer.currentTimeMillis();

	/** ChannelContext对象创建的时间 */
	private long timeCreated = SystemTimer.currentTimeMillis();

	/** 第一次连接成功的时间 */
	private Long timeFirstConnected = null;

	/** 连接关闭的时间 */
	private long timeClosed = SystemTimer.currentTimeMillis();

	/** 进入重连队列时间 */
	private long timeInReconnect = SystemTimer.currentTimeMillis();

	/** 本连接已发送的字节数 */
	private AtomicLong sentBytes = new AtomicLong();

	/** 本连接已发送的packet数 */
	private AtomicLong sentPackets = new AtomicLong();

	/** 本连接已处理的字节数 */
	private AtomicLong handledBytes = new AtomicLong();

	/** 本连接已处理的packet数 */
	private AtomicLong handledPackets = new AtomicLong();

	/** 本连接已接收的字节数 */
	private AtomicLong receivedBytes = new AtomicLong();

	/** 本连接已接收的packet数 */
	private AtomicLong receivedPackets = new AtomicLong();
}
