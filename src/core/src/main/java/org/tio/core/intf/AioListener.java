package org.tio.core.intf;

import org.tio.core.ChannelContext;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:34:08
 */
public interface AioListener {
	/**
	 * 连接关闭前后触发本方法
	 *
	 * @param channelContext the channelcontext
	 * @param throwable the throwable 有可能为空
	 * @param remark the remark 有可能为空
	 * @param isRemove 是否是删除
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception;

	/**
	 * 建链后触发本方法，注：建链不一定成功，需要关注参数isConnected
	 * @param channelContext
	 * @param isConnected 是否连接成功,true:表示连接成功，false:表示连接失败
	 * @param isReconnect 是否是重连, true: 表示这是重新连接，false: 表示这是第一次连接
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception;

	/**
	 * 解码成功后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterReceived(ChannelContext channelContext, Packet packet, int packetSize) throws Exception;

	/**
	 * 消息包发送之后触发本方法
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess true:发送成功，false:发送失败
	 * @throws Exception
	 * @author tanyaowu
	 *
	 */
	void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception;

	/**
	 * 连接关闭前触发本方法
	 *
	 * @param channelContext the channelcontext
	 * @param throwable the throwable 有可能为空
	 * @param remark the remark 有可能为空
	 * @param isRemove
	 * @author tanyaowu
	 */
	void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove);
}
