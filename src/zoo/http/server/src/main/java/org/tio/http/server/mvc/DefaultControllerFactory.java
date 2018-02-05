/**
 * 
 */
package org.tio.http.server.mvc;

import org.tio.http.server.mvc.intf.ControllerFactory;

/**
 * @author tanyw
 *
 */
public class DefaultControllerFactory implements ControllerFactory {
	
	public static final DefaultControllerFactory me = new DefaultControllerFactory();

	/**
	 * 
	 */
	private DefaultControllerFactory() {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	@Override
	public Object getInstance(Class<?> controllerClazz) throws Exception {
		return controllerClazz.newInstance();
	}

}
