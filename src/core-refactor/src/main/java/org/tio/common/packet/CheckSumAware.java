package org.tio.common.packet;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/16
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public interface CheckSumAware {
    /** 数据校验 */
    byte[][] checkSum();

    /** 数据校验 */
    void setCheckSum(byte checkSum);
}
