package org.tio.http.common;

import java.util.HashMap;
import java.util.Map;

import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpPacket extends Packet {

//	private static Logger log = LoggerFactory.getLogger(HttpPacket.class);

	public static final int MAX_LENGTH_OF_BODY = (int) (1024 * 1024 * 5.1); //只支持多少M数据

	protected byte[] body;
	
	private String headerString;

	protected Map<String, String> headers = new HashMap<>();

	public HttpPacket() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void removeHeader(String key, String value) {
		headers.remove(key);
	}

	/**
	 * @return the headers
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public String getHeaderString() {
		return headerString;
	}

	public void setHeaderString(String headerString) {
		this.headerString = headerString;
	}
}
