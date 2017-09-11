package org.tio.common;

import lombok.extern.slf4j.Slf4j;
import org.tio.server.ServerGroupContext;

/**
 * Copyright (c) for darkidiot
 * Date:2017/9/8
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc:
 */
public abstract class ServerState {

    protected ServerGroupContext context;

    public void setContext(ServerGroupContext _context) {
        this.context = _context;
    }

    /**
     * 启动 t-io server
     */
    abstract void start();

    /**
     * 关闭 t-io server
     */
    abstract void stop();
}

@Slf4j
class StartServer extends ServerState{
    private ServerGroupContext context;

    public void start() {
        context.status = CoreConstant.Status.STARTING;

        context.status = CoreConstant.Status.RUNING;
    }

    public void stop() {
        log.error("can not stop server now, because it was not running.");
    }
}

@Slf4j
class StopServer extends ServerState {
    private ServerGroupContext context;

    public void start() {
        log.error("can not stop server now, because it was not running.");
    }

    public void stop() {
        context.status = CoreConstant.Status.STOPPING;

        context.status = CoreConstant.Status.STOPPED;
    }
}