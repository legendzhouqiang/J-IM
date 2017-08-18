package org.tio.common;

import lombok.Getter;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 网络节点
 */
public class Node {

    @Getter
    private String ip;
    @Getter
    private int port;

    public Node(String ip, int port) {
        String trimIp = ip == null ? "" : ip.trim();
        if (trimIp.length() == 0) {
            ip = "0.0.0.0";
        }
        this.ip = ip;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return formatNode().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return ip.equals(other.getIp()) && port == other.getPort();
        }
        return false;
    }

    @Override
    public String toString() {
        return formatNode();
    }

    private String formatNode() {
        return String.format("%s:%d", this.ip, this.port);
    }
}
