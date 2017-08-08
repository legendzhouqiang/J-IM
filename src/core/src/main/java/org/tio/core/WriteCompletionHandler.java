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
import org.tio.core.intf.PacketWithMeta;
import org.tio.core.stat.GroupStat;

/**
 * 
 * @author tanyaowu 
 *
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, WriteCompletionVo> {

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
		} else {
			handle(result, null, writeCompletionVo);
		}
		
	}

	@Override
	public void failed(Throwable throwable, WriteCompletionVo writeCompletionVo) {
//		Object attachment = writeCompletionVo.getObj();
		handle(0, throwable, writeCompletionVo);
	}

	/**
	 * 
	 * @param result
	 * @param throwable
	 * @param attachment Packet or PacketWithMeta or List<PacketWithMeta> or List<Packet>
	 * @author: tanyaowu
	 */
	public void handle(Integer result, Throwable throwable, WriteCompletionVo writeCompletionVo) {
		this.writeSemaphore.release();
		Object attachment = writeCompletionVo.getObj();
		
		GroupContext groupContext = channelContext.getGroupContext();
		GroupStat groupStat = groupContext.getGroupStat();
		ChannelStat channelStat = channelContext.getStat();
		//		AioListener aioListener = groupContext.getAioListener();
		boolean isSentSuccess = result > 0;

		if (isSentSuccess) {
			groupStat.getSentBytes().addAndGet(result);
			channelStat.getSentBytes().addAndGet(result);
		}

		int packetCount = 0;
		try {
			boolean isPacket = attachment instanceof Packet;
			boolean isPacketWithMeta = !isPacket && attachment instanceof PacketWithMeta;

			if (isPacket || isPacketWithMeta) {
				if (isSentSuccess) {
					groupStat.getSentPacket().incrementAndGet();
					channelStat.getSentPackets().incrementAndGet();
				}
				handleOne(result, throwable, attachment, isSentSuccess);
			} else {
				List<?> ps = (List<?>) attachment;
				if (isSentSuccess) {
					packetCount = ps.size();
					groupStat.getSentPacket().addAndGet(packetCount);
					channelStat.getSentPackets().addAndGet(packetCount);
				}

				for (Object obj : ps) {
					handleOne(result, throwable, obj, isSentSuccess);
				}
			}

			if (!isSentSuccess) {
				Aio.close(channelContext, throwable, "写数据返回:" + result);
			}
		} catch (Exception e) {
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
	 * @author: tanyaowu
	 */
	public void handleOne(Integer result, Throwable throwable, Object obj, Boolean isSentSuccess) {
		Packet packet = null;
		PacketWithMeta packetWithMeta = null;

		boolean isPacket = obj instanceof Packet;
		if (isPacket) {
			packet =  (Packet)obj;
		} else {
			packetWithMeta = (PacketWithMeta) obj;
			packetWithMeta.setIsSentSuccess(isSentSuccess);
			packet = packetWithMeta.getPacket();
		}

		try {
			channelContext.traceClient(ChannelAction.AFTER_SEND, packet, null);
			channelContext.processAfterSent(obj, isSentSuccess);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

	}

	/**
	 * @return the writeSemaphore
	 */
	public java.util.concurrent.Semaphore getWriteSemaphore() {
		return writeSemaphore;
	}
	
	public static class WriteCompletionVo {
		/**
		 * @param byteBuffer
		 * @param obj
		 * @author: tanyaowu
		 */
		public WriteCompletionVo(ByteBuffer byteBuffer, Object obj) {
			super();
			this.byteBuffer = byteBuffer;
			this.obj = obj;
		}
		private ByteBuffer byteBuffer = null;
		private Object obj = null;
		/**
		 * @return the byteBuffer
		 */
		public ByteBuffer getByteBuffer() {
			return byteBuffer;
		}
		/**
		 * @param byteBuffer the byteBuffer to set
		 */
		public void setByteBuffer(ByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}
		/**
		 * @return the obj
		 */
		public Object getObj() {
			return obj;
		}
		/**
		 * @param obj the obj to set
		 */
		public void setObj(Object obj) {
			this.obj = obj;
		}
	}

}
