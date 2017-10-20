package org.tio.core;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ReconnConf;
import org.tio.core.intf.AioListener;
import org.tio.core.maintain.MaintainUtils;
import org.tio.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:39:59
 */
public class CloseRunnable implements Runnable {

	private static Logger log = LoggerFactory.getLogger(CloseRunnable.class);

	private ChannelContext channelContext;
	private Throwable throwable;
	private String remark;
	private boolean isNeedRemove;

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年3月1日 下午1:52:12
	 *
	 */
	public CloseRunnable(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
		this.channelContext = channelContext;
		this.throwable = throwable;
		this.remark = remark;
		this.isNeedRemove = isNeedRemove;
	}

	/**
	 * @see java.lang.Runnable#run()
	 *
	 * @author tanyaowu
	 * 2017年3月1日 下午1:54:34
	 *
	 */
	@Override
	public void run() {
		try {
			GroupContext groupContext = channelContext.getGroupContext();
			AioListener aioListener = groupContext.getAioListener();

			try {
				AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
				if (asynchronousSocketChannel != null && asynchronousSocketChannel.isOpen()) {
					try {
						asynchronousSocketChannel.close();
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}

			boolean isClientChannelContext = channelContext instanceof ClientChannelContext;
			//			ReconnConf reconnConf = channelContext.getGroupContext().getReconnConf();
			boolean isRemove = this.isNeedRemove;
			if (!isRemove) {
				if (isClientChannelContext) {
					ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;

					if (!ReconnConf.isNeedReconn(clientChannelContext, false)) {
						isRemove = true;
					}
				} else {
					isRemove = true;
				}
			}

			try {
				channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
				aioListener.onBeforeClose(channelContext, throwable, remark, isRemove);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}

			ReentrantReadWriteLock reentrantReadWriteLock = channelContext.getCloseLock();//.getLock();
			WriteLock writeLock = reentrantReadWriteLock.writeLock();
			boolean isLock = writeLock.tryLock();

			try {
				if (!isLock) {
					if (isRemove) {
						if (channelContext.isRemoved()) {
							return;
						} else {
							writeLock.lock();
						}
					} else {
						return;
					}
				}

				channelContext.traceClient(ChannelAction.UNCONNECT, null, null);

				if (channelContext.isClosed() && !isRemove) {
					log.info("{}, {}已经关闭，备注:{}，异常:{}", channelContext.getGroupContext(), channelContext, remark, throwable == null ? "无" : throwable.toString());
					return;
				}

				if (channelContext.isRemoved()) {
					log.info("{}, {}已经删除，备注:{}，异常:{}", channelContext.getGroupContext(), channelContext, remark, throwable == null ? "无" : throwable.toString());
					return;
				}

				//必须先取消任务再清空队列
				//				channelContext.getDecodeRunnable().setCanceled(true);
				channelContext.getHandlerRunnable().setCanceled(true);
				//		channelContext.getHandlerRunnableHighPrior().setCanceled(true);
				channelContext.getSendRunnable().setCanceled(true);
				//		channelContext.getSendRunnableHighPrior().setCanceled(true);

				channelContext.getDecodeRunnable().clearMsgQueue();
				channelContext.getHandlerRunnable().clearMsgQueue();
				//		channelContext.getHandlerRunnableHighPrior().clearMsgQueue();
				channelContext.getSendRunnable().clearMsgQueue();
				//		channelContext.getSendRunnableHighPrior().clearMsgQueue();

				log.info("{}, {} 准备关闭连接, isNeedRemove:{}, {}", channelContext.getGroupContext(), channelContext, isRemove, remark);

				try {
//					channelContext.getIpStat().getActivatedCount().decrementAndGet();
					
//					GuavaCache[] caches = channelContext.getGroupContext().ips.getCaches();
//					for (GuavaCache guavaCache : caches) {
//						IpStat ipStat = (IpStat) guavaCache.get(channelContext.getClientNode().getIp());
//						ipStat.getActivatedCount().decrementAndGet();
//					}
					
//					List<Long> list = groupContext.ips.list;
//					for (Long v : list) {
//						IpStat ipStat = (IpStat) channelContext.getGroupContext().ips.get(v, channelContext.getClientNode().getIp());
//						ipStat.getActivatedCount().decrementAndGet();
//					}
//					String clientIp = channelContext.getClientNode().getIp();
//					AtomicInteger activatedCount = IpStat.getActivatedCount(clientIp, false);
//					if (activatedCount != null) {
//						activatedCount.decrementAndGet();
//						if (activatedCount.get() <= 0) {
//							IpStat.removeActivatedCount(clientIp);
//						}
//					}
					
					if (isRemove) {
						MaintainUtils.removeFromMaintain(channelContext);
					} else {
//						if (!groupContext.isShortConnection()) {
							groupContext.closeds.add(channelContext);
							groupContext.connecteds.remove(channelContext);

							if (StringUtils.isNotBlank(channelContext.getUserid())) {
								try {
									Aio.unbindUser(channelContext);
								} catch (Throwable e) {
									log.error(e.toString(), e);
								}
							}

							try {
								Aio.unbindGroup(channelContext);
							} catch (Throwable e) {
								log.error(e.toString(), e);
							}
//						}
					}

					try {
						
						channelContext.setRemoved(isRemove);
						channelContext.getGroupContext().getGroupStat().getClosed().incrementAndGet();
						channelContext.getStat().setTimeClosed(SystemTimer.currentTimeMillis());
					} catch (Exception e) {
						log.error(e.toString(), e);
					}

					try {
						aioListener.onAfterClose(channelContext, throwable, remark, isRemove);
						channelContext.setClosed(true);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}					
				} catch (Throwable e) {
					log.error(e.toString(), e);
				} finally {
					if (!isRemove && channelContext.isClosed() && isClientChannelContext) //不删除且没有连接上，则加到重连队列中
					{
						ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
						ReconnConf.put(clientChannelContext);
					}
				}
			} catch (Exception e) {
				log.error(throwable.toString(), e);
			} finally {
				writeLock.unlock();
			}
		} finally {
			channelContext.setWaitingClose(false);
		}
	}

}
