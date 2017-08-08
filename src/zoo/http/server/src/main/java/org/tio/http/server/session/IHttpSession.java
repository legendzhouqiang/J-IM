package org.tio.http.server.session;

/**
 * @author tanyaowu 
 * 2017年8月5日 上午10:40:03
 */
public interface IHttpSession {
	/**
	 * 设置会话属性
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	public void setAtrribute(String key, Object value);

	/**
	 * 获取会话属性
	 * @param key
	 * @return
	 * @author: tanyaowu
	 */
	public Object getAtrribute(String key);
	
	/**
	 * 清空所有属性
	 * 
	 * @author: tanyaowu
	 */
	public void clear();
}
