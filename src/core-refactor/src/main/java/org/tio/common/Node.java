package org.tio.common;

import lombok.Data;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
@Data
public class Node {

    private String ip;
    private int port;

    public Node(String ip,String port) {

    }
}
