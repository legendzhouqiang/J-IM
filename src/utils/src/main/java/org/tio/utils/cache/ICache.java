package org.tio.utils.cache;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author tanyaowu 
 * 2017年8月10日 上午11:38:26
 */
public interface ICache {
	/**
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	public void put(String key, Serializable value);

	/**
	 * @param id
	 * @return
	 * @author: tanyaowu
	 */
	public void remove(String key);

	/**
	 * @param id
	 * @return
	 * @author: tanyaowu
	 */
	public Serializable get(String key);

	Collection<String> keys();

	void clear();
}
