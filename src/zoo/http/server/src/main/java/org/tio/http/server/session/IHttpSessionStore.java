package org.tio.http.server.session;

/**
 * @author tanyaowu 
 * 2017年8月5日 上午10:40:03
 */
public interface IHttpSessionStore {
	/**
	 * 
	 * @param sessionId
	 * @param session
	 * @author: tanyaowu
	 */
	public void save(String sessionId, HttpSession session);

	/**
	 * 
	 * @param sessionId
	 * @return
	 * @author: tanyaowu
	 */
	public void remove(String sessionId);

	/**
	 * 
	 * @param sessionId
	 * @return
	 * @author: tanyaowu
	 */
	public HttpSession get(String sessionId);
}
