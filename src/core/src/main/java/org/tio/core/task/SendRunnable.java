package org.tio.core.task;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.WriteCompletionHandler;
import org.tio.core.WriteCompletionHandler.WriteCompletionVo;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.ssl.SslUtils;
import org.tio.core.ssl.SslVo;
import org.tio.core.utils.AioUtils;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:19:18
 */
public class SendRunnable extends AbstractQueueRunnable<Packet> {

	private static final Logger log = LoggerFactory.getLogger(SendRunnable.class);

	/** The msg queue. */
	private ConcurrentLinkedQueue<Packet> forSendAfterSslHandshakeCompleted = null;//new ConcurrentLinkedQueue<>();

	public ConcurrentLinkedQueue<Packet> getForSendAfterSslHandshakeCompleted(boolean forceCreate) {
		if (forceCreate && forSendAfterSslHandshakeCompleted == null) {
			synchronized (this) {
				if (forSendAfterSslHandshakeCompleted == null) {
					forSendAfterSslHandshakeCompleted = new ConcurrentLinkedQueue<>();
				}
			}
		}

		return forSendAfterSslHandshakeCompleted;
	}

	private ChannelContext channelContext = null;

	//SSL加密锁
	//	private Object sslEncryptLock = new Object();

	/**
	 *
	 * @param channelContext
	 * @param executor
	 * @author tanyaowu
	 */
	public SendRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
	}

	/**
	 * obj is PacketWithMeta or Packet
	 */
	@Override
	public boolean addMsg(Packet obj) {
		if (this.isCanceled()) {
			log.error("{}, 任务已经取消，{}添加到发送队列失败", channelContext, obj);
			return false;
		}
		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
		if (sslFacadeContext != null && !sslFacadeContext.isHandshakeCompleted() && SslUtils.needSslEncrypt(obj, channelContext)) {
			return this.getForSendAfterSslHandshakeCompleted(true).add(obj);
		} else {
			return msgQueue.add(obj);
		}
	}

	/**
	 * 清空消息队列
	 */
	@Override
	public void clearMsgQueue() {
		Packet p = null;
		forSendAfterSslHandshakeCompleted = null;
		while ((p = msgQueue.poll()) != null) {
			try {
				channelContext.processAfterSent(p, false);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
		}
	}

	private ByteBuffer getByteBuffer(Packet packet, GroupContext groupContext, AioHandler aioHandler) {
		ByteBuffer byteBuffer = packet.getPreEncodedByteBuffer();
		if (byteBuffer != null) {
			//			byteBuffer = byteBuffer.duplicate();
		} else {
			byteBuffer = aioHandler.encode(packet, groupContext, channelContext);
		}

		if (byteBuffer.position() != 0) {
			byteBuffer.flip();
		}
		return byteBuffer;
	}

	/**
	 * 新旧值是否进行了切换
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private static boolean swithed(Boolean oldValue, boolean newValue) {
		if (oldValue == null) {
			return false;
		}

		if (Objects.equals(oldValue, newValue)) {
			return false;
		}

		return true;
	}

	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}
		
		int listInitialCapacity = Math.min(queueSize, 200);

		GroupContext groupContext = this.channelContext.getGroupContext();
		AioHandler aioHandler = groupContext.getAioHandler();
		boolean isSsl = SslUtils.isSsl(channelContext);
		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();

		int maxCapacity = 1024 * 100;
		if (isSsl) {
//			maxCapacity = 1024 * 8;
		}

		Packet packet = null;
		List<Packet> packets = new ArrayList<>(listInitialCapacity);
		List<ByteBuffer> byteBuffers = new ArrayList<>(listInitialCapacity);
		int packetCount = 0;
		int allBytebufferCapacity = 0;
		Boolean needSslEncrypted = null;
		boolean sslSwitched = false;
		while ((packet = msgQueue.poll()) != null) {
			ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, aioHandler);

			packets.add(packet);
			byteBuffers.add(byteBuffer);
			packetCount++;
			allBytebufferCapacity += byteBuffer.limit();

			if (isSsl) {
				if (packet.isSslEncrypted()) {
					boolean _needSslEncrypted = false;
					sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				} else {
					boolean _needSslEncrypted = true;
					sslSwitched = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				}
			} else { //非ssl，不涉及到加密和不加密的切换
				needSslEncrypted = false;
			}

			if ((allBytebufferCapacity >= maxCapacity) || sslSwitched) {
				break;
			}
		}

		if (allBytebufferCapacity == 0) {
			return;
		}
		ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
		for (ByteBuffer byteBuffer : byteBuffers) {
			allByteBuffer.put(byteBuffer);
		}

		allByteBuffer.flip();

		if (needSslEncrypted) {
			SslVo sslVo = new SslVo(allByteBuffer, packets);
			try {
				sslFacadeContext.getSslFacade().encrypt(sslVo);
				allByteBuffer = sslVo.getByteBuffer();
			} catch (SSLException e) {
				log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
				Aio.close(channelContext, "进行SSL加密时发生了异常");
				return;
			}
		}

		this.sendByteBuffer(allByteBuffer, packetCount, packets);
	}

	/**
	 *
	 * @param byteBuffer
	 * @param packetCount
	 * @param packets Packet or PacketWithMeta or List<PacketWithMeta> or List<Packet>
	 * @author tanyaowu
	 */
	public void sendByteBuffer(ByteBuffer byteBuffer, Integer packetCount, Object packets) {
		if (byteBuffer == null) {
			log.error("{},byteBuffer is null", channelContext);
			return;
		}

		if (!AioUtils.checkBeforeIO(channelContext)) {
			return;
		}

		if (byteBuffer.position() != 0) {
			byteBuffer.flip();
		}

		AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
		WriteCompletionHandler writeCompletionHandler = channelContext.getWriteCompletionHandler();
		try {
			//			long start = SystemTimer.currentTimeMillis();
			writeCompletionHandler.getWriteSemaphore().acquire();
			//			long end = SystemTimer.currentTimeMillis();
			//			long iv = end - start;
			//			if (iv > 100) {
			//				//log.error("{} 等发送锁耗时:{} ms", channelContext, iv);
			//			}

		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}

		WriteCompletionVo writeCompletionVo = new WriteCompletionVo(byteBuffer, packets);
		asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, writeCompletionHandler);
	}

	//	/**
	//	 *
	//	 * @param obj Packet or PacketWithMeta
	//	 * @author tanyaowu
	//	 */
	//	public void sendPacket(Object obj) {
	//		Packet packet = null;
	//		PacketWithMeta packetWithMeta = null;
	//
	//		boolean isPacket = obj instanceof Packet;
	//		if (isPacket) {
	//			packet = (Packet) obj;
	//		} else {
	//			packetWithMeta = (PacketWithMeta) obj;
	//			packet = packetWithMeta.getPacket();
	//		}
	//
	//		channelContext.traceClient(ChannelAction.BEFORE_SEND, packet, null);
	//		GroupContext groupContext = channelContext.getGroupContext();
	//		ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, groupContext.getAioHandler());
	//		int packetCount = 1;
	//
	//		if (isPacket) {
	//			sendByteBuffer(byteBuffer, packetCount, packet);
	//		} else {
	//			sendByteBuffer(byteBuffer, packetCount, packetWithMeta);
	//		}
	//	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

}
