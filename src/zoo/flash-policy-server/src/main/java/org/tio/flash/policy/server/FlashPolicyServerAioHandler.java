package org.tio.flash.policy.server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.exception.LengthOverflowException;
import org.tio.core.intf.Packet;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.server.intf.ServerAioHandler;

/**
 * 
 * @author tanyaowu 
 * 2017年10月31日 下午4:27:31
 */
public class FlashPolicyServerAioHandler implements ServerAioHandler {
	private static Logger log = LoggerFactory.getLogger(FlashPolicyServerAioHandler.class);

	/**
	 * 处理消息
	 */
	@Override
	public void handler(Packet packet, ChannelContext channelContext) throws Exception {
		Aio.send(channelContext, FlashPolicyPacket.RESPONSE);
	}

	public static final String REQUEST_STR = "<policy-file-request/>";

	/**
	 * <policy-file-request/>
	 * @param buffer
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	@Override
	public FlashPolicyPacket decode(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		int readableLength = buffer.limit() - buffer.position();
		//收到的数据组不了业务包，则返回null以告诉框架数据不够
		if (readableLength < FlashPolicyPacket.MIN_LENGHT) {
			return null;
		}

		String line = null;

		try {
			line = ByteBufferUtils.readLine(buffer, Const.CHARSET, '\0', FlashPolicyPacket.MAX_LING_LENGHT);
		} catch (LengthOverflowException e) {
			throw new AioDecodeException(e);
		}

		if (line == null) {
			return null;
		} else {
			log.info("收到消息:{}", line);
			if (REQUEST_STR.equalsIgnoreCase(line)) {
				return FlashPolicyPacket.REQUEST;
			} else {
				throw new AioDecodeException("");
			}
		}
	}

	public static byte[] RESPONSE_BYTES;
	
	static {
		try {
			byte[] bs = ("<cross-domain-policy>"
					+ "<site-control permitted-cross-domain-policies='master-only'/>"
					+ "<allow-access-from domain='*' to-ports='*'/></cross-domain-policy>")
					.getBytes("UTF-8");
			
//			byte[] bs = ("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>").getBytes("UTF-8");
			byte endByte = 0x00;
			RESPONSE_BYTES = new byte[bs.length + 1];
			System.arraycopy(bs, 0, RESPONSE_BYTES, 0, bs.length);
			RESPONSE_BYTES[bs.length] = endByte;
			
			
		     
		
			
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 
	 * @param packet
	 * @param groupContext
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
		ByteBuffer ret = ByteBuffer.wrap(RESPONSE_BYTES);
//		ret.position(ret.limit());
		return ret;
	}

}
