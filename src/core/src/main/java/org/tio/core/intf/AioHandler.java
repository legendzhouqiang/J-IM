package org.tio.core.intf;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;

/**
 * The Interface AioHandler.
 *
 * @param <Ext> the generic type
 * @param  the generic type
 * @param <R> the generic type
 */
public interface AioHandler {

	/**
	 * 处理消息包
	 *
	 * @param packet the packet
	 * @return the r
	 * @author: tanyaowu
	 */
	void handler(Packet packet, ChannelContext channelContext) throws Exception;

	/**
	 * 编码
	 *
	 * @param packet the packet
	 * @return the byte buffer
	 * @author: tanyaowu
	 */
	ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext);

	/**
	 * 根据ByteBuffer解码成业务需要的Packet对象.
	 *
	 * @param buffer the buffer
	 * @return the t
	 * @throws AioDecodeException the aio decode exception
	 */
	Packet decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException;

}
