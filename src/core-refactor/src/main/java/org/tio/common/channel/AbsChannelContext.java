package org.tio.common.channel;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.tio.common.CoreConstant;
import org.tio.common.GroupStat;

import java.nio.ByteOrder;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import static org.tio.common.TioParams.*;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">darkidiot</a>
 * Desc: 分组上下文超类
 */
@Data
@Slf4j
public abstract class AbsChannelContext implements ChannelContext{

    /** synchronised thread pool core size */
    private static final int CORE_POOL_SIZE = Integer.getInteger(default_params_core_pool_size, Runtime.getRuntime().availableProcessors() * 2);
    /** synchronised thread pool max size */
    private static final int MAX_POOL_SIZE = Integer.getInteger(default_params_max_pool_size, CORE_POOL_SIZE * 4 < 256 ? 256 : CORE_POOL_SIZE * 4);
    /** synchronised thread pool keep alive time */
    public static final long ALIVE_TIME = Long.getLong(default_params_alive_time, 90L);

    /** 默认的接收数据的buffer size */
    public static final int READ_BUFFER_SIZE = Integer.getInteger(default_params_read_buffer_size, CoreConstant.default_receive_buf_size);

    /** 原子ID生成器 */
    private final static AtomicInteger ID_ATOMIC = new AtomicInteger();
    /** AbsChannelContext 唯一标识 */
    protected int id = ID_ATOMIC.incrementAndGet();

    /** 网络字节序对齐方式 */
    private ByteOrder byteOrder = CoreConstant.default_byte_order;

    /** statistics */
    private GroupStat statistics;

    /** status */
    protected CoreConstant.Status status = CoreConstant.Status.Init;

    /** The group executor. */
//    protected ThreadPoolExecutor tioExecutor = null;

    /** The group executor. */
    protected ThreadPoolExecutor aioExecutor = null;
}