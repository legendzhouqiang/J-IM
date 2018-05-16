/**
 * 
 */
package org.tio.utils.convert;

/**
 * @author tanyw
 * 从F类型转到T类型
 */
public interface Converter<T> {
	/**
	 * 
	 * @param value
	 * @return
	 */
	public T convert(Object value);

}
