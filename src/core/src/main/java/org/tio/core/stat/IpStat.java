package org.tio.core.stat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.utils.SystemTimer;

import com.xiaoleilu.hutool.date.BetweenFormater;
import com.xiaoleilu.hutool.date.BetweenFormater.Level;

/**
 * 这个是给服务器用的，主要用于监控IP情况，随时拉黑恶意攻击IP
 * @author tanyaowu 
 * 2017年8月20日 下午8:02:41
 */
public class IpStat implements java.io.Serializable {

	private static final long serialVersionUID = -6942731710053482089L;

	public IpStat(String ip) {
		this.ip = ip;
	}

	private Date start = new Date();

	private long duration;

	private String ip;

	/**
	 * 解码异常的次数
	 */
	private AtomicInteger decodeErrorCount = new AtomicInteger();

	/**
	 * 收到该IP连接请求的次数
	 */
	private AtomicInteger requestCount = new AtomicInteger();

	/**
	 * 当前处于连接状态的个数
	 */
	private AtomicInteger activatedCount = new AtomicInteger();

	/**
	 * 本IP已发送的字节数
	 */
	private AtomicLong sentBytes = new AtomicLong();

	/**
	 * 本IP已发送的packet数
	 */
	private AtomicLong sentPackets = new AtomicLong();
	
	/**
	 * 本IP已处理的字节数
	 */
	private AtomicLong handledBytes = new AtomicLong();

	/**
	 * 本IP已处理的packet数
	 */
	private AtomicLong handledPackets = new AtomicLong();

	/**
	 * 本IP已接收的字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();
	
	/**
	 * 本IP已接收了多少次TCP数据包
	 */
	private AtomicLong receivedTcps = new AtomicLong();

	/**
	 * 本IP已接收的packet数
	 */
	private AtomicLong receivedPackets = new AtomicLong();

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
	 * @return the decodeErrorCount
	 */
	public AtomicInteger getDecodeErrorCount() {
		return decodeErrorCount;
	}

	/**
	 * @return the countHandledByte
	 */
	public AtomicLong getHandledBytes() {
		return handledBytes;
	}

	/**
	 * @return the countHandledPacket
	 */
	public AtomicLong getHandledPackets() {
		return handledPackets;
	}

	/**
	 * @return the countReceivedByte
	 */
	public AtomicLong getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @return the countReceivedPacket
	 */
	public AtomicLong getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @return the countSentByte
	 */
	public AtomicLong getSentBytes() {
		return sentBytes;
	}

	/**
	 * @return the countSentPacket
	 */
	public AtomicLong getSentPackets() {
		return sentPackets;
	}

	/**
	 * @param decodeErrorCount the decodeErrorCount to set
	 */
	public void setDecodeErrorCount(AtomicInteger decodeErrorCount) {
		this.decodeErrorCount = decodeErrorCount;
	}

	/**
	 * @param countHandledByte the countHandledByte to set
	 */
	public void setHandledBytes(AtomicLong countHandledByte) {
		this.handledBytes = countHandledByte;
	}

	/**
	 * @param countHandledPacket the countHandledPacket to set
	 */
	public void setHandledPackets(AtomicLong handledPackets) {
		this.handledPackets = handledPackets;
	}

	/**
	 * @param countReceivedByte the countReceivedByte to set
	 */
	public void setReceivedBytes(AtomicLong receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	/**
	 * @param countReceivedPacket the countReceivedPacket to set
	 */
	public void setReceivedPackets(AtomicLong receivedPackets) {
		this.receivedPackets = receivedPackets;
	}

	/**
	 * @param countSentByte the countSentByte to set
	 */
	public void setSentBytes(AtomicLong sentBytes) {
		this.sentBytes = sentBytes;
	}

	/**
	 * @param countSentPacket the countSentPacket to set
	 */
	public void setSentPackets(AtomicLong sentPackets) {
		this.sentPackets = sentPackets;
	}

	/**
	 * @return the requestCount
	 */
	public AtomicInteger getRequestCount() {
		return requestCount;
	}

	/**
	 * @param requestCount the requestCount to set
	 */
	public void setRequestCount(AtomicInteger requestCount) {
		this.requestCount = requestCount;
	}

	/**
	 * @return the activatedCount
	 */
	public AtomicInteger getActivatedCount() {
		return activatedCount;
	}

	/**
	 * @param activatedCount the activatedCount to set
	 */
	public void setActivatedCount(AtomicInteger activatedCount) {
		this.activatedCount = activatedCount;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the duration
	 */
	public String getFormatedDuration() {
		duration = SystemTimer.currentTimeMillis() - this.start.getTime();
		BetweenFormater betweenFormater = new BetweenFormater(duration, Level.MILLSECOND);
		return betweenFormater.format();
	}

	public long getDuration() {
		duration = SystemTimer.currentTimeMillis() - this.start.getTime();
		return duration;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
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
}
