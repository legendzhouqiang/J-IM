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

		//		if (SslUtils.needSslEncrypt(obj, channelContext)) {
		//			SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
		//
		//			if (sslFacadeContext.isHandshakeCompleted()) {
		//				Packet p = PacketUtils.getPacket(obj);
		//				GroupContext groupContext = this.channelContext.getGroupContext();
		//				AioHandler aioHandler = groupContext.getAioHandler();
		//				ByteBuffer byteBuffer = aioHandler.encode(p, groupContext, channelContext);
		//				byteBuffer.flip();
		//				try {
		//					synchronized (sslEncryptLock) {
		//						sslFacadeContext.getSslFacade().encrypt(new SslVo(byteBuffer, obj));
		//					}
		//				} catch (SSLException e) {
		//					log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
		//					Aio.close(channelContext, "进行SSL加密时发生了异常");
		//					return false;
		//				}
		//			} else {
		//				this.getForSendAfterSslHandshakeCompleted(true).add(obj);
		//			}
		//
		//			return true;
		//		} else {
		
		
		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
		if (sslFacadeContext != null && !sslFacadeContext.isHandshakeCompleted() && SslUtils.needSslEncrypt(obj, channelContext)) {
			return this.getForSendAfterSslHandshakeCompleted(true).add(obj);
		} else {
			return msgQueue.add(obj);
		}
		
		
		//		}
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
		
		
		
		
		GroupContext groupContext = this.channelContext.getGroupContext();
		AioHandler aioHandler = groupContext.getAioHandler();
		boolean isSsl = SslUtils.isSsl(channelContext);
		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
		
		Packet packet = null;
		List<Packet> packets = new ArrayList<>(queueSize);
		List<ByteBuffer> byteBuffers = new ArrayList<>(queueSize);
		int packetCount = 0;
		int allBytebufferCapacity = 0;
		Boolean needSslEncrypted = null;
		boolean needBreak = false;
		while ((packet = msgQueue.poll()) != null) {
			ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, aioHandler);
			
			packets.add(packet);
			byteBuffers.add(byteBuffer);
			packetCount++;
			allBytebufferCapacity += byteBuffer.limit();
			
			if (isSsl) {
				if (packet.isSslEncrypted()) {
					boolean _needSslEncrypted = false;
					needBreak = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				} else {
					boolean _needSslEncrypted = true;
					needBreak = swithed(needSslEncrypted, _needSslEncrypted);
					needSslEncrypted = _needSslEncrypted;
				}
				
				if (needBreak) {
					break;
				}
			} else {  //非ssl，不涉及到加密和不加密的切换
				needSslEncrypted = false;
			}
			
			
		}
		
		
		
		
		ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
//		byte[] dest = allByteBuffer.array();
		for (ByteBuffer byteBuffer : byteBuffers) {
//			int length = byteBuffer.limit();
//			int position = allByteBuffer.position();
//			System.arraycopy(byteBuffer.array(), 0, dest, position, length);
//			allByteBuffer.position(position + length);
			
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
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

//		if (queueSize >= 2000) {
//			queueSize = 1000;
//		}
//		
////		queueSize = 1;
//
//		SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
//		if (SslUtils.isSsl(channelContext) && !sslFacadeContext.isHandshakeCompleted()) {
//			queueSize = 1;
//		}
//
//		//Packet or PacketWithMeta
//		
//		
//		
//		if (queueSize > 1) {//如果是SSL：到这里的，肯定是没有经过SSL加密的
//			ByteBuffer[] byteBuffers = new ByteBuffer[queueSize];
//			int allBytebufferCapacity = 0;
//
//			int packetCount = 0;
//			List<Object> packets = new ArrayList<>(queueSize);
//			for (int i = 0; i < queueSize; i++) {
//				Object obj = null;
//				if ((obj = msgQueue.poll()) != null) {
//					Packet p = null;
//					PacketWithMeta packetWithMeta = null;
//					boolean isPacket = obj instanceof Packet;
//					if (isPacket) {
//						p = (Packet) obj;
//						packets.add(p);
//					} else {
//						packetWithMeta = (PacketWithMeta) obj;
//						p = packetWithMeta.getPacket();
//						packets.add(packetWithMeta);
//					}
//
//					ByteBuffer byteBuffer = getByteBuffer(p, groupContext, aioHandler);
//
//					channelContext.traceClient(ChannelAction.BEFORE_SEND, p, null);
//
//					allBytebufferCapacity += byteBuffer.limit();
//					packetCount++;
//					byteBuffers[i] = byteBuffer;
//				} else {
//					break;
//				}
//			}
//
//			ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
//			log.info("本次将发送的字节数:{}, 包数:{}", allBytebufferCapacity, packetCount);
//			byte[] dest = allByteBuffer.array();
//			for (ByteBuffer byteBuffer : byteBuffers) {
//				if (byteBuffer != null) {
//					int length = byteBuffer.limit();
//					int position = allByteBuffer.position();
//					System.arraycopy(byteBuffer.array(), 0, dest, position, length);
//					allByteBuffer.position(position + length);
//				}
//			}
//			
//			allByteBuffer.flip();
//			SslVo sslVo = new SslVo(allByteBuffer, packets);
//			try {
//				sslFacadeContext.getSslFacade().encrypt(sslVo);
//				ByteBuffer encryptedByteBuffer = sslVo.getByteBuffer();
//				sendByteBuffer(encryptedByteBuffer, packetCount, packets);
//			} catch (SSLException e) {
//				log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
//				Aio.close(channelContext, "进行SSL加密时发生了异常");
//				return;
//			}
//		} else {  //单条
//			Object obj = null;
//			if ((obj = msgQueue.poll()) != null) {
//				Packet p = PacketUtils.getPacket(obj);
//				int packetCount = 1;
//				if (SslUtils.needSslEncrypt(obj, channelContext)) {
//					SslVo sslVo = new SslVo(null, obj);
//					ByteBuffer plaintByteBuffer = aioHandler.encode(p, groupContext, channelContext);
//					plaintByteBuffer.flip();
//					sslVo.setByteBuffer(plaintByteBuffer);
//					try {
//						sslFacadeContext.getSslFacade().encrypt(sslVo);
//						ByteBuffer encryptedByteBuffer = sslVo.getByteBuffer();
//						sendByteBuffer(encryptedByteBuffer, packetCount, obj);
//					} catch (SSLException e) {
//						log.error(channelContext.toString() + ", 进行SSL加密时发生了异常", e);
//						Aio.close(channelContext, "进行SSL加密时发生了异常");
//						return;
//					}
//				} else {
//					groupContext = channelContext.getGroupContext();
//					ByteBuffer byteBuffer = getByteBuffer(p, groupContext, groupContext.getAioHandler());
//					sendByteBuffer(byteBuffer, packetCount, obj);
//				}
//			}
//		}
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
