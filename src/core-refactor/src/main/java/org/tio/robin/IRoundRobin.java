package org.tio.robin;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/27
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:  轮询扩展/收缩接口
 */
public interface IRoundRobin extends IRobin {

    /** 扩展 */
    IRoundRobin expand(String str);

    /** 收缩 */
    IRoundRobin shrink(String str);
}
