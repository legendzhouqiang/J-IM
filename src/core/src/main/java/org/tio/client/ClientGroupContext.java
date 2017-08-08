package org.tio.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.GroupContext;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.stat.GroupStat;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:31:31
 */
public class ClientGroupContext extends GroupContext {
	static Logger log = LoggerFactory.getLogger(ClientGroupContext.class);

	private ClientAioHandler clientAioHandler = null;

	private ClientAioListener clientAioListener = null;

	private ClientGroupStat clientGroupStat = new ClientGroupStat();

	private ConnectionCompletionHandler connectionCompletionHandler = new ConnectionCompletionHandler();

	/**
	 * 不重连
	 * @param aioHandler
	 * @param aioListener
	 * @author: tanyaowu
	 */
	public ClientGroupContext(ClientAioHandler aioHandler, ClientAioListener aioListener) {
		this(aioHandler, aioListener, null);
	}

	/**
	 * 
	 * @param aioHandler
	 * @param aioListener
	 * @param reconnConf 不需要自动重连就传null
	 * @author: tanyaowu
	 */
	public ClientGroupContext(ClientAioHandler aioHandler, ClientAioListener aioListener, ReconnConf reconnConf) {
		super();

		this.setClientAioHandler(aioHandler);
		this.setClientAioListener(aioListener);

		this.reconnConf = reconnConf;
	}

	/**
	 * @param clientGroupStat the clientGroupStat to set
	 */
	public void setClientGroupStat(ClientGroupStat clientGroupStat) {
		this.clientGroupStat = clientGroupStat;
	}

	public ClientGroupStat getClientGroupStat() {
		return clientGroupStat;
	}

	/**
	 * @return the clientAioHandler
	 */
	public ClientAioHandler getClientAioHandler() {
		return clientAioHandler;
	}

	/**
	 * @param clientAioHandler the clientAioHandler to set
	 */
	public void setClientAioHandler(ClientAioHandler clientAioHandler) {
		this.clientAioHandler = clientAioHandler;
	}

	/**
	 * @return the clientAioListener
	 */
	public ClientAioListener getClientAioListener() {
		return clientAioListener;
	}

	/**
	 * @param clientAioListener the clientAioListener to set
	 */
	public void setClientAioListener(ClientAioListener clientAioListener) {
		this.clientAioListener = clientAioListener;
		if (this.clientAioListener == null) {
			this.clientAioListener = new DefaultClientAioListener();
		}
	}

	/** 
	 * @see org.tio.core.GroupContext#getAioHandler()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:33:46
	 * 
	 */
	@Override
	public AioHandler getAioHandler() {
		return this.getClientAioHandler();
	}

	/** 
	 * @see org.tio.core.GroupContext#getGroupStat()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:33:46
	 * 
	 */
	@Override
	public GroupStat getGroupStat() {
		return this.getClientGroupStat();
	}

	/** 
	 * @see org.tio.core.GroupContext#getAioListener()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:33:46
	 * 
	 */
	@Override
	public AioListener getAioListener() {
		return this.getClientAioListener();
	}

	/**
	 * @param reconnConf the reconnConf to set
	 */
	public void setReconnConf(ReconnConf reconnConf) {
		this.reconnConf = reconnConf;
	}

	/**
	 * @return the connectionCompletionHandler
	 */
	public ConnectionCompletionHandler getConnectionCompletionHandler() {
		return connectionCompletionHandler;
	}

	/**
	 * @param connectionCompletionHandler the connectionCompletionHandler to set
	 */
	public void setConnectionCompletionHandler(ConnectionCompletionHandler connectionCompletionHandler) {
		this.connectionCompletionHandler = connectionCompletionHandler;
	}

}
