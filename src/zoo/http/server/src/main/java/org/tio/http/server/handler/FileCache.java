package org.tio.http.server.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2017年8月15日 下午5:44:52
 */
public class FileCache implements java.io.Serializable {

	private static final long serialVersionUID = 6517890350387789902L;

	private static Logger log = LoggerFactory.getLogger(FileCache.class);
	//this.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding, "gzip");
	private Map<String, String> headers = null;
	private long lastModified;
	private byte[] data;

	public FileCache(Map<String, String> headers, long lastModified, byte[] data) {
		super();
		this.setHeaders(headers);
		this.lastModified = lastModified;
		this.data = data;
	}

	/**
	 * 
	 * @author: tanyaowu
	 */
	public FileCache() {
	}

	

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}


}
