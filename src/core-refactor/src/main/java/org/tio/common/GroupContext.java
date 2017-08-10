package org.tio.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/**
 * Copyright (c) for 谭耀武
 * Date:2017/8/10
 * Author: <a href="tywo45@163.com">谭耀武</a>
 * Desc:
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

    /** GroupContext 唯一标识 */
    protected String id;

    protected GroupStat statis;

}