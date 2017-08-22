package org.tio.core.task;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelAction;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.PacketHandlerMode;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;
import org.tio.core.stat.ChannelStat;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.utils.SystemTimer;

/**
 * 解码
 *
 * @author 谭耀武
 * 2012-08-09
 *
 */
public class DecodeRunnable implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(DecodeRunnable.class);

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param byteCount
	 * @author tanyaowu
	 */
	public static void handler(ChannelContext channelContext, Packet packet, int byteCount) {

		GroupContext groupContext = channelContext.getGroupContext();
		PacketHandlerMode packetHandlerMode = groupContext.getPacketHandlerMode();

		HandlerRunnable handlerRunnable = channelContext.getHandlerRunnable();
		if (packetHandlerMode == PacketHandlerMode.QUEUE) {

			handlerRunnable.addMsg(packet);
			groupContext.getTioExecutor().execute(handlerRunnable);
		} else {
			handlerRunnable.handler(packet);
		}
	}

	private ChannelContext channelContext = null;

	/**
	 * 上一次解码剩下的数据
	 */
	private ByteBuffer lastByteBuffer = null;

	/**
	 * 新收到的数据
	 */
	private ByteBuffer newByteBuffer = null;

	/**
	 *
	 */
	public DecodeRunnable(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	/**
	 * 清空处理的队列消息
	 */
	public void clearMsgQueue() {
		lastByteBuffer = null;
		newByteBuffer = null;
	}

	/**
	 * @see java.lang.Runnable#run()
	 *
	 * @author tanyaowu
	 * 2017年3月21日 下午4:26:39
	 *
	 */
	@Override
	public void run() {
		ByteBuffer byteBuffer = newByteBuffer;
		if (byteBuffer != null) {
			if (lastByteBuffer != null) {
				byteBuffer = ByteBufferUtils.composite(lastByteBuffer, byteBuffer);
				lastByteBuffer = null;
			}
		} else {
			return;
		}

		try {
			label_2: while (true) {
				int initPosition = byteBuffer.position();
				Packet packet = channelContext.getGroupContext().getAioHandler().decode(byteBuffer, channelContext);

				if (packet == null)// 数据不够，解不了码
				{
					lastByteBuffer = ByteBufferUtils.copy(byteBuffer, initPosition, byteBuffer.limit());
					ChannelStat channelStat = channelContext.getStat();
					channelStat.setDecodeFailCount(channelStat.getDecodeFailCount() + 1);
					int len = byteBuffer.capacity() - initPosition;
					log.info("{} 解码失败, 本次共失败{}次，参与解码的数据长度共{}字节", channelContext, channelStat.getDecodeFailCount(), len);
					if (channelStat.getDecodeFailCount() > 5) {
						log.error("{} 解码失败, 本次共失败{}次，参与解码的数据长度共{}字节，请考虑要不要拉黑这个ip", channelContext, channelStat.getDecodeFailCount(), len);

					}
					return;
				} else //解码成功
				{
					channelContext.getStat().setLatestTimeOfReceivedPacket(SystemTimer.currentTimeMillis());

					ChannelStat channelStat = channelContext.getStat();
					channelStat.setDecodeFailCount(0);

					int afterDecodePosition = byteBuffer.position();
					int len = afterDecodePosition - initPosition;

					//					if (len == 0)
					//					{
					//						String logstr = channelContext + "解码成功, " + packet.logstr() + "," + byteBuffer + " 但是却只消耗了0字节, 这有可能会导致死循环. " + ThreadUtils.stackTrace();
					//						log.error(logstr);
					//					}

					channelContext.getGroupContext().getGroupStat().getReceivedPacket().incrementAndGet();
					channelContext.getGroupContext().getGroupStat().getReceivedBytes().addAndGet(len);

					channelContext.getStat().getReceivedPackets().incrementAndGet();
					channelContext.getStat().getReceivedBytes().addAndGet(len);
					
					channelContext.getIpStat().getReceivedPackets().incrementAndGet();
					channelContext.getIpStat().getReceivedBytes().addAndGet(len);

					channelContext.traceClient(ChannelAction.RECEIVED, packet, null);

					packet.setByteCount(len);

					AioListener aioListener = channelContext.getGroupContext().getAioListener();
					try {
						if (log.isInfoEnabled()) {
							log.info("{} 收到消息 {}", channelContext, packet.logstr());
						}
						aioListener.onAfterReceived(channelContext, packet, len);
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
					handler(channelContext, packet, len);

					int remainingLength = byteBuffer.limit() - byteBuffer.position();
					if (remainingLength > 0)//组包后，还剩有数据
					{
						if (log.isDebugEnabled()) {
							log.debug("{},组包后，还剩有数据:{}", channelContext, remainingLength);
						}
						continue label_2;
					} else//组包后，数据刚好用完
					{
						lastByteBuffer = null;
						log.debug("{},组包后，数据刚好用完", channelContext);
						return;
					}
				}
			}
		} catch (AioDecodeException e) {
//			log.error(channelContext.toString() + "解码异常", e);
			Aio.close(channelContext, e, "解码异常:" + e.getMessage());
			int errorCount = channelContext.getIpStat().getDecodeErrorCount().incrementAndGet();
			if (errorCount > 10) {
				
			}
			return;
		}
	}

	/**
	 * @param newByteBuffer the newByteBuffer to set
	 */
	public void setNewByteBuffer(ByteBuffer newByteBuffer) {
		this.newByteBuffer = newByteBuffer;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

}
