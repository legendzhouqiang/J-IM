package org.tio.robin;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/27
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 加权轮询扩展/收缩接口
 */
public interface IWeightRoundRobin extends IRobin {

    /** 扩展;weight定义:0-9 */
    IWeightRoundRobin expand(String val, int weight);

    /** 收缩 */
    IWeightRoundRobin shrink(String val);
}
