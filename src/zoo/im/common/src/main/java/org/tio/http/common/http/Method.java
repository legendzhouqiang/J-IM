package org.tio.http.common.http;

/**
 * @author tanyaowu 
 * 2017年6月28日 下午2:23:16
 */
public enum Method {
	HEAD("HEAD"), PUT("PUT"), TRACE("TRACE"), OPTIONS("OPTIONS"), PATCH("PATCH"), GET("GET"), POST("POST");
	String value;

	private Method(String value) {
		this.value = value;
	}

	public static Method from(String method) {
		Method[] values = Method.values();
		for (Method v : values) {
			if (v.value == method) {
				return v;
			}
		}
		return GET;
	}
}
