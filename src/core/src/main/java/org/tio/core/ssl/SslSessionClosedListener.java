package org.tio.core.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.ssl.facade.ISessionClosedListener;

public class SslSessionClosedListener implements ISessionClosedListener {
	private static Logger log = LoggerFactory.getLogger(SslSessionClosedListener.class);
	private ChannelContext channelContext;

	public SslSessionClosedListener(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void onSessionClosed() {
//		log.info("{} onSessionClosed", channelContext);
		Aio.close(channelContext, "SSL SessionClosed");
	}

}
