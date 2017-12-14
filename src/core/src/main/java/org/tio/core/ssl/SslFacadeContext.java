/**
 * 
 */
package org.tio.core.ssl;

import javax.net.ssl.SSLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ssl.facade.DefaultTaskHandler;
import org.tio.core.ssl.facade.ISSLFacade;
import org.tio.core.ssl.facade.SSLFacade;
import org.tio.server.ServerChannelContext;

/**
 * @author tanyw
 *
 */
public class SslFacadeContext {
	private static Logger log = LoggerFactory.getLogger(SslFacadeContext.class);

	private ChannelContext channelContext = null;

	private SSLContext sslContext;

	private ISSLFacade sslFacade = null;

	//ssl握手是否已经完成, true: 已经完成， false: 还没有完成
	private boolean isHandshakeCompleted = false;

	/**
	 * 
	 * @param channelContext
	 * @throws Exception
	 */
	public SslFacadeContext(ChannelContext channelContext) throws Exception {
		this.channelContext = channelContext;
		this.channelContext.setSslFacadeContext(this);
		
		this.isHandshakeCompleted = false;

		SslConfig sslConfig = channelContext.getGroupContext().getSslConfig();

		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(sslConfig.getKeyManagerFactory().getKeyManagers(), sslConfig.getTrustManagerFactory().getTrustManagers(), null);

		DefaultTaskHandler taskHandler = new DefaultTaskHandler();

		boolean isClient = true;
		if (this.channelContext instanceof ServerChannelContext) {  //server mode
			isClient = false;
		}

		sslFacade = new SSLFacade(this.channelContext, sslContext, isClient, false, taskHandler);
		sslFacade.setHandshakeCompletedListener(new SslHandshakeCompletedListener(this.channelContext));
		sslFacade.setSSLListener(new SslListener(this.channelContext));
		sslFacade.setCloseListener(new SslSessionClosedListener(this.channelContext));
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void beginHandshake() throws Exception {
		log.info("{} 开始SSL握手", channelContext);
		sslFacade.beginHandshake();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	public boolean isHandshakeCompleted() {
		return isHandshakeCompleted;
	}

	public void setHandshakeCompleted(boolean isHandshakeCompleted) {
		this.isHandshakeCompleted = isHandshakeCompleted;
	}

	public ChannelContext getChannelContext() {
		return channelContext;
	}

	public SSLContext getSslContext() {
		return sslContext;
	}

	public ISSLFacade getSslFacade() {
		return sslFacade;
	}

}
