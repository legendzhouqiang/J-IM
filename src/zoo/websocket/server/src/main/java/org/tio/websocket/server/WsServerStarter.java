package org.tio.websocket.server;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.threadpool.SynThreadPoolExecutor;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.common.WsTioUuid;
import org.tio.websocket.server.handler.IWsMsgHandler;

/**
 * 
 * @author tanyaowu 
 * 2017年7月30日 上午9:45:54
 */
public class WsServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(WsServerStarter.class);

	/**
	 * 
	 * 
	 * @author: tanyaowu
	 */
	public WsServerStarter() {

	}

	private WsServerConfig wsServerConfig = null;

	private IWsMsgHandler wsMsgHandler = null;

	private WsServerAioHandler wsServerAioHandler = null;

	private WsServerAioListener wsServerAioListener = null;

	private ServerGroupContext serverGroupContext = null;

	private AioServer aioServer = null;

	public void start(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
		this.wsServerConfig = wsServerConfig;
		this.wsMsgHandler = wsMsgHandler;
		wsServerAioHandler = new WsServerAioHandler(wsServerConfig, wsMsgHandler);
		wsServerAioListener = new WsServerAioListener();
		serverGroupContext = new ServerGroupContext(wsServerAioHandler, wsServerAioListener, tioExecutor, groupExecutor);
		serverGroupContext.setHeartbeatTimeout(0);

		aioServer = new AioServer(serverGroupContext);

		WsTioUuid wsTioUuid = new WsTioUuid();
		serverGroupContext.setTioUuid(wsTioUuid);

		aioServer.start(wsServerConfig.getBindIp(), wsServerConfig.getBindPort());
	}

	public void start(int port, IWsMsgHandler wsMsgHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
		this.wsServerConfig = new WsServerConfig(port);
		start(wsServerConfig, wsMsgHandler, tioExecutor, groupExecutor);
	}

	public void start(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) throws IOException {
		this.start(wsServerConfig, wsMsgHandler, null, null);
	}

	public void start(int port, IWsMsgHandler wsMsgHandler) throws IOException {
		this.start(port, wsMsgHandler, null, null);
	}

	/**
	 * @return the wsServerAioHandler
	 */
	public WsServerAioHandler getHttpServerAioHandler() {
		return wsServerAioHandler;
	}

	/**
	 * @return the wsServerAioListener
	 */
	public WsServerAioListener getHttpServerAioListener() {
		return wsServerAioListener;
	}
	
	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	/**
	 * @return the wsServerConfig
	 */
	public WsServerConfig getHttpServerConfig() {
		return wsServerConfig;
	}

	/**
	 * @return the wsMsgHandler
	 */
	public IWsMsgHandler getHttpRequestHandler() {
		return wsMsgHandler;
	}
}
