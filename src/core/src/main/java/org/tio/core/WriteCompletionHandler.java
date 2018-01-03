package org.tio.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.WriteCompletionHandler.WriteCompletionVo;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.core.stat.ChannelStat;
import org.tio.core.stat.GroupStat;
import org.tio.core.stat.IpStat;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 *
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, WriteCompletionVo> {

	public static class WriteCompletionVo {
		private ByteBuffer byteBuffer = null;

		private Object obj = null;

		/**
		 * @param byteBuffer
		 * @param obj
		 * @author tanyaowu
		 */
		public WriteCompletionVo(ByteBuffer byteBuffer, Object obj) {
			super();
			this.byteBuffer = byteBuffer;
			this.obj = obj;
		}

		/**
		 * @return the byteBuffer
		 */
		public ByteBuffer getByteBuffer() {
			return byteBuffer;
		}

		/**
		 * @return the obj
		 */
		public Object getObj() {
			return obj;
		}

		/**
		 * @param byteBuffer the byteBuffer to set
		 */
		public void setByteBuffer(ByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}

		/**
		 * @param obj the obj to set
		 */
		public void setObj(Object obj) {
			this.obj = obj;
		}
	}

	private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);

	private ChannelContext channelContext = null;

	private java.util.concurrent.Semaphore writeSemaphore = new Semaphore(1);

	public WriteCompletionHandler(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void completed(Integer result, WriteCompletionVo writeCompletionVo) {
		//		Object attachment = writeCompletionVo.getObj();
		ByteBuffer byteBuffer = writeCompletionVo.getByteBuffer();
		if (byteBuffer.hasRemaining()) {
			//			int iv = byteBuffer.capacity() - byteBuffer.position();
			log.info("{} {}/{} has sent", channelContext, byteBuffer.position(), byteBuffer.capacity());
			AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
			asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, this);
			channelContext.getStat().setLatestTimeOfSentByte(SystemTimer.currentTimeMillis());
		} else {
			channelContext.getStat().setLatestTimeOfSentPacket(SystemTimer.currentTimeMillis());
			handle(result, null, writeCompletionVo);
		}

	}

	@Override
	public void failed(Throwable throwable, WriteCompletionVo writeCompletionVo) {
		//		Object attachment = writeCompletionVo.getObj();
		handle(0, throwable, writeCompletionVo);
	}

	/**
	 * @return the writeSemaphore
	 */
	public java.util.concurrent.Semaphore getWriteSemaphore() {
		return writeSemaphore;
	}

	/**
	 *
	 * @param result
	 * @param throwable
	 * @param attachment Packet or PacketWithMeta or List<PacketWithMeta> or List<Packet>
	 * @author tanyaowu
	 */
	public void handle(Integer result, Throwable throwable, WriteCompletionVo writeCompletionVo) {
		this.writeSemaphore.release();
		Object attachment = writeCompletionVo.getObj();

		GroupContext groupContext = channelContext.getGroupContext();
		GroupStat groupStat = groupContext.getGroupStat();
		ChannelStat channelStat = channelContext.getStat();
		//		AioListener aioListener = groupContext.getAioListener();
		boolean isSentSuccess = result > 0;

		//		CaffeineCache[] caches = channelContext.getGroupContext().ips.getCaches();

		List<Long> list = groupContext.ipStats.durationList;

		if (isSentSuccess) {
			groupStat.getSentBytes().addAndGet(result);
			channelStat.getSentBytes().addAndGet(result);
			//			channelContext.getIpStat().getSentBytes().addAndGet(result);

			//			for (CaffeineCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(channelContext.getClientNode().getIp());
			//				ipStat.getSentBytes().addAndGet(result);
			//			}
			for (Long v : list) {
				IpStat ipStat = (IpStat) channelContext.getGroupContext().ipStats.get(v, channelContext.getClientNode().getIp());
				ipStat.getSentBytes().addAndGet(result);
			}

		}

//		int packetCount = 0;
		try {
			boolean isPacket = attachment instanceof Packet;
			if (isPacket) {
				if (isSentSuccess) {

					//					channelContext.getIpStat().getSentPackets().incrementAndGet();

					//					for (CaffeineCache guavaCache : caches) {
					//						IpStat ipStat = (IpStat) guavaCache.get(channelContext.getClientNode().getIp());
					//						ipStat.getSentPackets().incrementAndGet();
					//					}

					for (Long v : list) {
						IpStat ipStat = (IpStat) channelContext.getGroupContext().ipStats.get(v, channelContext.getClientNode().getIp());
						ipStat.getSentPackets().incrementAndGet();
					}
				}
				handleOne(result, throwable, (Packet)attachment, isSentSuccess);
			} else {
				List<?> ps = (List<?>) attachment;
//				if (isSentSuccess) {
//					packetCount = ps.size();
//				}

				for (Object obj : ps) {
					handleOne(result, throwable, (Packet)obj, isSentSuccess);
				}
			}

			if (!isSentSuccess) {
				Aio.close(channelContext, throwable, "写数据返回:" + result);
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {

		}
	}

	/**
	 *
	 * @param result
	 * @param throwable
	 * @param obj PacketWithMeta or Packet
	 * @param isSentSuccess
	 * @author tanyaowu
	 */
	public void handleOne(Integer result, Throwable throwable, Packet packet, Boolean isSentSuccess) {
		Meta meta = packet.getMeta();
		
		if (meta != null) {
			meta.setIsSentSuccess(isSentSuccess);
		}

		try {
			channelContext.traceClient(ChannelAction.AFTER_SEND, packet, null);
			channelContext.processAfterSent(packet, isSentSuccess);

			GroupContext groupContext = channelContext.getGroupContext();
			GroupStat groupStat = groupContext.getGroupStat();
			ChannelStat channelStat = channelContext.getStat();

			List<Long> list = groupContext.ipStats.durationList;
			for (Long v : list) {
				IpStat ipStat = (IpStat) channelContext.getGroupContext().ipStats.get(v, channelContext.getClientNode().getIp());
				ipStat.getSentPackets().incrementAndGet();
			}

			groupStat.getSentPackets().incrementAndGet();
			channelStat.getSentPackets().incrementAndGet();

		} catch (Throwable e) {
			log.error(e.toString(), e);
		}

	}

}
