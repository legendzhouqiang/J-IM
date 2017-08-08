package org.tio.http.server.session;

import org.tio.core.GroupContext;

/**
 * 不能免俗地用了一把工厂模式，虽然内容并不喜欢^_^
 * @author tanyaowu 
 * 2017年8月5日 上午11:39:51
 */
public interface IHttpSessionFactory<T extends IHttpSession> {
	/**
	 * 
	 * @param sessionId
	 * @param sessionTimeout
	 * @param groupContext
	 * @return
	 * @author: tanyaowu
	 */
	T create(String sessionId, Long sessionTimeout, GroupContext groupContext);
}
