package org.tio.core.udp;

import org.tio.core.Node;
import org.tio.core.udp.intf.UdpHandler;

/**
 * @author tanyaowu 
 * 2017年7月5日 下午3:53:04
 */
public class UdpServerConf extends UdpConf {
	//	private static Logger log = LoggerFactory.getLogger(UdpServerConf.class);

	private UdpHandler udpHandler;

	private int readBufferSize = 1024 * 1024;

	public UdpServerConf(int port, UdpHandler udpHandler, int timeout) {
		super(timeout);
		this.setUdpHandler(udpHandler);
		this.setServerNode(new Node(null, port));
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	public UdpHandler getUdpHandler() {
		return udpHandler;
	}

	public void setUdpHandler(UdpHandler udpHandler) {
		this.udpHandler = udpHandler;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}
}
