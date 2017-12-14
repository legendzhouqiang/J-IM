package org.tio.core;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.ssl.SslVo;
import org.tio.core.stat.IpStat;
import org.tio.core.task.DecodeRunnable;
import org.tio.core.utils.AioUtils;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 * 2017年4月4日 上午9:22:04
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
	private static Logger log = LoggerFactory.getLogger(ReadCompletionHandler.class);
	private ChannelContext channelContext = null;
	private ByteBuffer readByteBuffer;

	//	private ByteBuffer byteBuffer = ByteBuffer.allocate(ChannelContext.READ_BUFFER_SIZE);

	/**
	 *
	 * @param channelContext
	 * @author tanyaowu
	 */
	public ReadCompletionHandler(ChannelContext channelContext) {
		this.channelContext = channelContext;
		this.readByteBuffer = ByteBuffer.allocate(channelContext.getGroupContext().getReadBufferSize());
	}

	@Override
	public void completed(Integer result, ByteBuffer byteBuffer) {
		//		GroupContext groupContext = channelContext.getGroupContext();
		if (result > 0) {
			channelContext.getStat().setLatestTimeOfReceivedByte(SystemTimer.currentTimeMillis());
			GroupContext groupContext = channelContext.getGroupContext();

			groupContext.getGroupStat().getReceivedBytes().addAndGet(result);
			channelContext.getStat().getReceivedBytes().addAndGet(result);
			//			channelContext.getIpStat().getReceivedBytes().addAndGet(result);

			groupContext.getGroupStat().getReceivedTcps().incrementAndGet();
			channelContext.getStat().getReceivedTcps().incrementAndGet();
			//			channelContext.getIpStat().getReceivedTcps().incrementAndGet();

			//			GuavaCache[] caches = groupContext.ips.getCaches();
			//			for (GuavaCache guavaCache : caches) {
			//				IpStat ipStat = (IpStat) guavaCache.get(channelContext.getClientNode().getIp());
			//				ipStat.getReceivedBytes().addAndGet(result);
			//				ipStat.getReceivedTcps().incrementAndGet();
			//			}
			List<Long> list = groupContext.ipStats.durationList;
			for (Long v : list) {
				IpStat ipStat = (IpStat) groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
				ipStat.getReceivedBytes().addAndGet(result);
				ipStat.getReceivedTcps().incrementAndGet();
			}

			if (channelContext.isTraceClient()) {
				Map<String, Object> map = new HashMap<>(10);
				map.put("p_r_buf_len", result);
				channelContext.traceClient(ChannelAction.RECEIVED_BUF, null, map);
			}

			//			ByteBuffer newByteBuffer = ByteBufferUtils.copy(readByteBuffer, 0, readByteBuffer.position());

			SslFacadeContext sslFacadeContext = channelContext.getSslFacadeContext();
			if (sslFacadeContext == null) {
				DecodeRunnable decodeRunnable = channelContext.getDecodeRunnable();
				readByteBuffer.flip();
				decodeRunnable.setNewByteBuffer(readByteBuffer);
				decodeRunnable.run();
			} else {
				try {
					ByteBuffer copiedByteBuffer = ByteBufferUtils.copy(readByteBuffer, 0, readByteBuffer.position());
					log.info("{}, 丢给SslFacade解密:{}", channelContext, copiedByteBuffer);
					sslFacadeContext.getSslFacade().decrypt(copiedByteBuffer);
				} catch (SSLException e) {
					log.error(channelContext + ", " + e.toString(), e);
					Aio.close(channelContext, e, e.toString());
				}
			}
			//			decodeRunnable.addMsg(newByteBuffer);
			//			groupContext.getDecodeExecutor().execute(decodeRunnable);
		} else if (result == 0) {
			log.error("{}, 读到的数据长度为0", channelContext);
		} else if (result < 0) {
			if (result == -1) {
				Aio.close(channelContext, null, "对方关闭了连接");
				return;
			} else {
				Aio.close(channelContext, null, "读数据时返回" + result);
				return;
			}
		}

		if (AioUtils.checkBeforeIO(channelContext)) {
			AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
			readByteBuffer.position(0);
			readByteBuffer.limit(readByteBuffer.capacity());
			asynchronousSocketChannel.read(readByteBuffer, readByteBuffer, this);
		}
	}

	/**
	 *
	 * @param exc
	 * @param byteBuffer
	 * @author tanyaowu
	 */
	@Override
	public void failed(Throwable exc, ByteBuffer byteBuffer) {
		Aio.close(channelContext, exc, "读数据时发生异常");
	}

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	public ByteBuffer getReadByteBuffer() {
		return readByteBuffer;
	}
}
