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
	 *
	 * @author tanyaowu
	 */
	void clear();

	/**
	 * @param id
	 * @return
	 * @author tanyaowu
	 */
	public Serializable get(String key);

	/**
	 *
	 * @return
	 * @author tanyaowu
	 */
	Collection<String> keys();

	/**
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	public void put(String key, Serializable value);

	/**
	 * @param id
	 * @return
	 * @author tanyaowu
	 */
	public void remove(String key);
}
