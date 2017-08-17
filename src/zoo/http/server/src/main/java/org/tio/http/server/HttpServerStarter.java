package org.tio.http.server;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpUuid;
import org.tio.http.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.http.server.handler.DefaultHttpRequestHandler;
import org.tio.http.server.handler.IHttpRequestHandler;
import org.tio.http.server.listener.IHttpServerListener;
import org.tio.http.server.mvc.Routes;
import org.tio.server.AioServer;
import org.tio.server.ServerGroupContext;
import org.tio.utils.cache.ICache;
import org.tio.utils.cache.guava.GuavaCache;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

import com.xiaoleilu.hutool.io.FileUtil;

/**
 * 
 * @author tanyaowu
 */
public class HttpServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpServerStarter.class);

	private HttpServerConfig httpConfig = null;

	private HttpServerAioHandler httpServerAioHandler = null;

	private HttpServerAioListener httpServerAioListener = null;

	//	private HttpGroupListener httpGroupListener = null;

	private ServerGroupContext serverGroupContext = null;

	private AioServer aioServer = null;

	private IHttpRequestHandler httpRequestHandler;

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 2016年11月17日 下午5:59:24
	 * 
	 */
	public static void main(String[] args) throws IOException {
	}

	private void init(HttpServerConfig httpConfig, IHttpRequestHandler httpRequestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this.httpConfig = httpConfig;
		this.httpRequestHandler = httpRequestHandler;
		this.httpServerAioHandler = new HttpServerAioHandler(httpConfig, httpRequestHandler);
		httpServerAioListener = new HttpServerAioListener();
		serverGroupContext = new ServerGroupContext(httpServerAioHandler, httpServerAioListener, tioExecutor, groupExecutor);
		serverGroupContext.setHeartbeatTimeout(1000 * 10);
		serverGroupContext.setShortConnection(true);

		aioServer = new AioServer(serverGroupContext);

		HttpUuid imTioUuid = new HttpUuid();
		serverGroupContext.setTioUuid(imTioUuid);
	}

	public HttpServerStarter(HttpServerConfig httpConfig, IHttpRequestHandler httpRequestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		init(httpConfig, httpRequestHandler, tioExecutor, groupExecutor);
	}

	public HttpServerStarter(HttpServerConfig httpConfig, IHttpRequestHandler httpRequestHandler) {
		this(httpConfig, httpRequestHandler, null, null);
	}

	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener) {
		this(pageRootDir, serverPort, scanPackages, httpServerListener, null, null, null);
	}

	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener, ICache sessionStore) {
		this(pageRootDir, serverPort, scanPackages, httpServerListener, sessionStore, null, null);
	}

	public HttpServerStarter(String pageRootDir, int serverPort, String[] scanPackages, IHttpServerListener httpServerListener, ICache sessionStore,
			SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		int port = serverPort;
		String pageRoot = pageRootDir;

		httpConfig = new HttpServerConfig(port, null);
		httpConfig.setRoot(pageRoot);
		if (sessionStore != null) {
			httpConfig.setSessionStore(sessionStore);
		}
		//		} else {
		//			httpConfig.setHttpSessionStore(GuavaHttpSessionStore.getInstance(httpConfig.getSessionTimeout()));
		//		}

		//		String[] scanPackages = new String[] { AppStarter.class.getPackage().getName() };
		Routes routes = new Routes(scanPackages);
		DefaultHttpRequestHandler httpRequestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
		httpRequestHandler.setHttpServerListener(httpServerListener);

		init(httpConfig, httpRequestHandler, tioExecutor, groupExecutor);
	}

	public void start() throws IOException {
		if (httpConfig.getSessionStore() == null) {
			GuavaCache guavaCache = GuavaCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
			httpConfig.setSessionStore(guavaCache);
		}

		if (httpConfig.getRoot() == null) {
			httpConfig.setRoot(FileUtil.getAbsolutePath("classpath:page"));
		}

		if (httpConfig.getSessionIdGenerator() == null) {
			httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
		}

		aioServer.start(this.httpConfig.getBindIp(), this.httpConfig.getBindPort());
	}

	public void stop() throws IOException {
		aioServer.stop();
	}

	/**
	 * @return the httpServerAioHandler
	 */
	public HttpServerAioHandler getHttpServerAioHandler() {
		return httpServerAioHandler;
	}

	/**
	 * @return the httpServerAioListener
	 */
	public HttpServerAioListener getHttpServerAioListener() {
		return httpServerAioListener;
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	/**
	 * @return the httpConfig
	 */
	public HttpServerConfig getHttpServerConfig() {
		return httpConfig;
	}

	public IHttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	public void setHttpRequestHandler(IHttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}
}
