package org.tio.utils.cache.gr;

import java.io.Serializable;

import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * @author tanyaowu 
 * 2017年8月12日 下午9:30:31
 */
public class CacheChangedVo implements Serializable {

	private static final long serialVersionUID = 1546804469064012259L;

	public static final String CLIENTID = RandomUtil.randomString(10);
	

	private String cacheName;
	private String key;
	/**
	 * @param cacheName
	 * @param type
	 * @author: tanyaowu
	 */
	public CacheChangedVo(String cacheName, CacheChangeType type) {
		super();
		this.cacheName = cacheName;
		this.type = type;
	}

	/**
	 * @param cacheName
	 * @param key
	 * @param type
	 * @author: tanyaowu
	 */
	public CacheChangedVo(String cacheName, String key, CacheChangeType type) {
		super();
		this.cacheName = cacheName;
		this.key = key;
		this.type = type;
	}

	private String clientId = CLIENTID;
//	private String serverId;
	private CacheChangeType type;

	//	private

	/**
	 * 
	 * @author: tanyaowu
	 */
	public CacheChangedVo() {
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the type
	 */
	public CacheChangeType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(CacheChangeType type) {
		this.type = type;
	}
}
