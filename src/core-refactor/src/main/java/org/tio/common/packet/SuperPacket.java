package org.tio.common.packet;

import java.io.Serializable;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/14
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 数据包接口定义
 */
public interface SuperPacket extends Serializable {

    /** 包头魔数 */
    short magic = -21829; //0xAABB complement number

    /** 包头总长 */
    int headerSize = 7;
}
