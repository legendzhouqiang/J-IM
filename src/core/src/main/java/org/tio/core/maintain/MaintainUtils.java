package org.tio.core.maintain;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:34
 */
public class MaintainUtils {
	private static Logger log = LoggerFactory.getLogger(MaintainUtils.class);

	/**
	 * 彻底删除，不再维护
	 * @param channelContext
	 *
	 * @author tanyaowu
	 *
	 */
	public static void remove(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.getGroupContext();

		groupContext.connections.remove(channelContext);
		groupContext.connecteds.remove(channelContext);
		groupContext.closeds.remove(channelContext);
		groupContext.ips.unbind(channelContext);

		//		if (groupContext.isShortConnection()) {
		//			return;
		//		}

		try {
			//id解绑
			groupContext.ids.unbind(channelContext);

			close(channelContext);
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
	}

	public static void close(ChannelContext channelContext) {
		//		GroupContext groupContext = channelContext.getGroupContext();

		//用户id解绑
		if (StringUtils.isNotBlank(channelContext.getUserid())) {
			try {
				Aio.unbindUser(channelContext);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
		}

		//token解绑
		if (StringUtils.isNotBlank(channelContext.getToken())) {
			try {
				Aio.unbindToken(channelContext);
			} catch (Throwable e) {
				log.error(e.toString(), e);
			}
		}

		//群组解绑
		try {
			Aio.unbindGroup(channelContext);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}

}
