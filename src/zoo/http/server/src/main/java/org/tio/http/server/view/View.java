package org.tio.http.server.view;

import org.tio.http.common.HttpRequest;

/**
 * 
 * @author tanyw
 *
 */
public interface View {
	
	/**
	 * 
	 * @return
	 */
	String[] getSuffixes();
	
	/**
	 * 
	 * @param path
	 * @param content
	 * @param request
	 * @return
	 */
	String output(String path, String content, HttpRequest request);
	
	/**
	 * 
	 * @return
	 */
	ModelGenerator getModelGenerator();
}
