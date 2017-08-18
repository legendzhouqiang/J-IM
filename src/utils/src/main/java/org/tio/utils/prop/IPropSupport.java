package org.tio.utils.prop;

/**
 * @author tanyaowu 
 * 2017年8月18日 下午5:34:14
 */
public interface IPropSupport {
	public void setAttribute(String key, Object value);

	public Object getAttribute(String key);

	public void removeAttribute(String key);
	
	public void clearAttribute();
}
