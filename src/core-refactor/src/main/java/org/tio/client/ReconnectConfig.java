package org.tio.client;

import lombok.Data;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 重连实体类
 */
@Data
public class ReconnectConfig {

    /** 重连的间隔时间，单位毫秒 */
    private long interval = 5000;

    /** 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连 */
    private int retryCount = 3;

}
