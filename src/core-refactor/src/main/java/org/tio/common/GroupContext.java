package org.tio.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc: 分组上下文超类
 */
@Data
@Slf4j
public abstract class GroupContext {

    /** synchronised thread pool core size */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    /** synchronised thread pool max size */
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 4 < 256 ? 256 : CORE_POOL_SIZE * 4;
    /** synchronised thread pool keep alive time */
    public static final long ALIVE_TIME = 90L;

    /** 默认的接收数据的buffer size */
    public static final int READ_BUFFER_SIZE = Integer.getInteger("tio.default.read.buffer.size", 2048);

    /** 原子ID生成器 */
    private final static AtomicInteger ID_ATOMIC = new AtomicInteger();
    /** GroupContext 唯一标识 */
    protected String id;

    /** 网络字节序对齐方式 */
    private ByteOrder byteOrder = CoreConstant.byteOrder;

    /** statistics */
    protected GroupStat statistics;

    /** The group executor. */
    protected ThreadPoolExecutor tioExecutor = null;

    /** The group executor. */
    protected ThreadPoolExecutor groupExecutor = null;

    /** 等待关闭 */
    private boolean waitingStop = false;

    /** 完成关闭 */
    private boolean isStopped = false;
}