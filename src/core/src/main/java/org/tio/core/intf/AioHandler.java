package org.tio.core.intf;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:15
 */
public interface AioHandler {

	/**
	 * 根据ByteBuffer解码成业务需要的Packet对象.
	 * 如果收到的数据不全，导致解码失败，请返回null，在下次消息来时框架层会自动续上前面的收到的数据
	 * @param buffer
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 * @author: tanyaowu
	 */
	Packet decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException;

	/**
	 * 编码
	 * @param packet
	 * @param groupContext
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext);

	/**
	 * 处理消息包
	 * @param packet
	 * @param channelContext
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void handler(Packet packet, ChannelContext channelContext) throws Exception;

}
