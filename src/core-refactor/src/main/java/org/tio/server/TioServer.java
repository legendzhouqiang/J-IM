package org.tio.server;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/18
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: t-io server
 */
public class TioServer {

    private ServerGroupContext context;

    public TioServer(ServerGroupContext context) {
        this.context = context;
    }

    /**
     * 启动 t-io server
     */
    public TioServer start() {
        context.start();
        return this;
    }

    /**
     * 关闭 t-io server
     */
    public TioServer stop() {
        context.start();
        return this;
    }


}
