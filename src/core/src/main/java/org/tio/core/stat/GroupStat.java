package org.tio.core.stat;

import java.util.concurrent.atomic.AtomicLong;

public class GroupStat {
	/**
	 * 关闭了多少连接
	 */
	private AtomicLong closed = new AtomicLong();
	/**
	 * 接收到的消息包
	 */
	private AtomicLong receivedPacket = new AtomicLong();
	/**
	 * 接收到的消息字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();
	/**
	 * 处理了的消息包数
	 */
	private AtomicLong handledPacket = new AtomicLong();

	private AtomicLong handledBytes = new AtomicLong();

	/**
	 * 发送了的消息包数
	 */
	private AtomicLong sentPacket = new AtomicLong();

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
	 * @return the handledPacket
	 */
	public AtomicLong getHandledPacket() {
		return handledPacket;
	}

	/**
	 * @return the receivedBytes
	 */
	public AtomicLong getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @return the receivedPacket
	 */
	public AtomicLong getReceivedPacket() {
		return receivedPacket;
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
	public AtomicLong getSentPacket() {
		return sentPacket;
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

}
