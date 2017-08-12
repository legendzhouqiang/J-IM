package org.tio.core.udp;

import org.tio.core.Node;

/**
 * @author tanyaowu 
 * 2017年7月5日 下午2:53:38
 */
public class UdpConf {
	//	private static Logger log = LoggerFactory.getLogger(UdpConf.class);

	private int timeout = 5000;

	private Node serverNode = null;

	private String charset = "utf-8";

	/**
	 * 
	 * @author: tanyaowu
	 */
	public UdpConf(int timeout) {
		this.setTimeout(timeout);
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	public Node getServerNode() {
		return serverNode;
	}

	public void setServerNode(Node serverNode) {
		this.serverNode = serverNode;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
