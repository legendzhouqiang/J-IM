package org.tio.utils.cache;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author tanyaowu
 * 2017年8月10日 上午11:38:26
 */
public interface ICache {

	/**
	 *
	 * 清空所有缓存
	 * @author tanyaowu
	 */
	void clear();

	/**
	 * 根据key获取value
	 * @param id
	 * @return
	 * @author tanyaowu
	 */
	public Serializable get(String key);

	/**
	 * 获取所有的key
	 * @return
	 * @author tanyaowu
	 */
	Collection<String> keys();

	/**
	 * 将key value保存到缓存中
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	public void put(String key, Serializable value);

	/**
	 * 删除一个key
	 * @param id
	 * @return
	 * @author tanyaowu
	 */
	public void remove(String key);
}
