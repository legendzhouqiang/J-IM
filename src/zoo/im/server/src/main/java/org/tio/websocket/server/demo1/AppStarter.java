package org.tio.websocket.server.demo1;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.websocket.server.WsServerStarter;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午5:34:04
 */
public class AppStarter {
	private static Logger log = LoggerFactory.getLogger(AppStarter.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public AppStarter() {
	}

	WsServerStarter wsServerStarter;
	WsMsgHandler wsMsgHandler;

	public void start(int port, WsMsgHandler wsMsgHandler) throws IOException {
		this.wsMsgHandler = wsMsgHandler;
		wsServerStarter = new WsServerStarter();
		wsServerStarter.start(port, wsMsgHandler);
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		AppStarter appStarter = new AppStarter();
		appStarter.start(9321, new WsMsgHandler());
	}
}
