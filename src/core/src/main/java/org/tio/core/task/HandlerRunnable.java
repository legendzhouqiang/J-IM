/**
 *
 */
package org.tio.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelAction;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.core.maintain.ChannelContextMapWithLock;
import org.tio.core.stat.IpStat;
import org.tio.core.stat.IpStatType;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 *
 * @author 谭耀武
 * 2012-08-09
 *
 */
public class HandlerRunnable extends AbstractQueueRunnable<Packet> {
	private static final Logger log = LoggerFactory.getLogger(HandlerRunnable.class);

	private ChannelContext channelContext = null;

	private AtomicLong synFailCount = new AtomicLong();

	public HandlerRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
	}

	/**
	 * 处理packet
	 * @param packet
	 * @return
	 *
	 * @author tanyaowu
	 */
	public void handler(Packet packet) {
		//		int ret = 0;

		GroupContext groupContext = channelContext.getGroupContext();
		try {

			Integer synSeq = packet.getSynSeq();
			if (synSeq != null && synSeq > 0) {
				ChannelContextMapWithLock syns = channelContext.getGroupContext().getWaitingResps();
				Packet initPacket = syns.remove(synSeq);
				if (initPacket != null) {
					synchronized (initPacket) {
						syns.put(synSeq, packet);
						initPacket.notify();
					}
				} else {
					log.error("[{}]同步消息失败, synSeq is {}, 但是同步集合中没有对应key值", synFailCount.incrementAndGet(), synSeq);
				}
			} else {
				channelContext.traceClient(ChannelAction.BEFORE_HANDLER, packet, null);
				groupContext.getAioHandler().handler(packet, channelContext);
				channelContext.traceClient(ChannelAction.AFTER_HANDLER, packet, null);
			}
			//			ret++;
		} catch (Exception e) {
			log.error(e.toString(), e);
			//			return ret;
		} finally {
			channelContext.getStat().getHandledPackets().incrementAndGet();
			channelContext.getStat().getHandledBytes().addAndGet(packet.getByteCount());

			groupContext.getGroupStat().getHandledPacket().incrementAndGet();
			groupContext.getGroupStat().getHandledBytes().addAndGet(packet.getByteCount());
			
//			channelContext.getIpStat().getHandledPackets().incrementAndGet();
//			channelContext.getIpStat().getHandledBytes().addAndGet(packet.getByteCount());
			
//			GuavaCache[] caches = channelContext.getGroupContext().ips.getCaches();
//			for (GuavaCache guavaCache : caches) {
//				IpStat ipStat = (IpStat) guavaCache.get(channelContext.getClientNode().getIp());
//				ipStat.getHandledPackets().incrementAndGet();
//				ipStat.getHandledBytes().addAndGet(packet.getByteCount());
//			}
			
			IpStatType[] ipStatTypes = IpStatType.values();
			for (IpStatType v : ipStatTypes) {
				IpStat ipStat = (IpStat) groupContext.ips.get(v, channelContext.getClientNode().getIp());
				ipStat.getHandledPackets().incrementAndGet();
				ipStat.getHandledBytes().addAndGet(packet.getByteCount());

			}
		}

		//		return ret;
	}

	/**
	 * @see org.tio.core.threadpool.intf.ISynRunnable#runTask()
	 *
	 * @author tanyaowu
	 * 2016年12月5日 下午3:02:49
	 *
	 */
	@Override
	public void runTask() {
		Packet packet = null;
		while ((packet = msgQueue.poll()) != null) {
			handler(packet);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}
}
