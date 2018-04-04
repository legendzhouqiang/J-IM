package org.tio.core.stat;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:48
 */
public class GroupStat implements java.io.Serializable  {
	private static final long serialVersionUID = -6988655941470121164L;
	/**
	 * 关闭了多少连接
	 */
	private AtomicLong closed = new AtomicLong();
	/**
	 * 接收到的消息包
	 */
	private AtomicLong receivedPackets = new AtomicLong();
	
	/**
	 * 接收到的消息字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();
	
	/**
	 * 本IP已接收了多少次TCP数据包
	 */
	private AtomicLong receivedTcps = new AtomicLong();
	
	/**
	 * 平均每次TCP接收到的字节数，这个可以用来监控慢攻击，配置PacketsPerTcpReceive定位慢攻击
	 */
	public double getBytesPerTcpReceive() {
		if (receivedTcps.get() == 0) {
			return 0;
		}
		double ret = (double)receivedBytes.get() / (double)receivedTcps.get();
		return ret;
	}
	
	/**
	 * 平均每次TCP接收到的业务包数，这个可以用来监控慢攻击，此值越小越有攻击嫌疑
	 */
	public double getPacketsPerTcpReceive() {
		if (receivedTcps.get() == 0) {
			return 0;
		}
		double ret = (double)receivedPackets.get() / (double)receivedTcps.get();
		return ret;
	}
	
	
	/**
	 * 处理了的消息包数
	 */
	private AtomicLong handledPackets = new AtomicLong();
	
	/**
	 * 处理消息包耗时，单位：毫秒
	 */
	private AtomicLong handledPacketCosts = new AtomicLong();

	/**
	 * 处理了多少字节
	 */
	private AtomicLong handledBytes = new AtomicLong();

	/**
	 * 发送了的消息包数
	 */
	private AtomicLong sentPackets = new AtomicLong();

	/**
	 * 发送了的字节数
	 */
	private AtomicLong sentBytes = new AtomicLong();

	/**
	 * @return the closed
	 */
	public AtomicLong getClosed() {
		return closed;
	}

	/**
	 * @return the handledBytes
	 */
	public AtomicLong getHandledBytes() {
		return handledBytes;
	}

	/**
	 * @return the handledPackets
	 */
	public AtomicLong getHandledPackets() {
		return handledPackets;
	}

	/**
	 * @return the receivedBytes
	 */
	public AtomicLong getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @return the receivedPackets
	 */
	public AtomicLong getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @return the sentBytes
	 */
	public AtomicLong getSentBytes() {
		return sentBytes;
	}

	/**
	 * @return the sentPacket
	 */
	public AtomicLong getSentPackets() {
		return sentPackets;
	}

	/**
	 * @param closed the closed to set
	 */
	public void setClosed(AtomicLong closed) {
		this.closed = closed;
	}

	/**
	 * @param handledBytes the handledBytes to set
	 */
	public void setHandledBytes(AtomicLong handledBytes) {
		this.handledBytes = handledBytes;
	}

	/**
	 * @param receivedBytes the receivedBytes to set
	 */
	public void setReceivedBytes(AtomicLong receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	/**
	 * @return the receivedTcps
	 */
	public AtomicLong getReceivedTcps() {
		return receivedTcps;
	}

	/**
	 * @param receivedTcps the receivedTcps to set
	 */
	public void setReceivedTcps(AtomicLong receivedTcps) {
		this.receivedTcps = receivedTcps;
	}

	/**
	 * 处理消息包耗时，单位：毫秒
	 * @return
	 */
	public AtomicLong getHandledPacketCosts() {
		return handledPacketCosts;
	}
	
	/**
	 * 处理packet平均耗时，单位：毫秒
	 * @return
	 */
	public double getHandledCostsPerPacket() {
		if (handledPackets.get() > 0) {
			return handledPacketCosts.get() / handledPackets.get();
		}
		return 0;
	}
}
