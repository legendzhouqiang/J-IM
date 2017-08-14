package org.tio.http.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.ZipUtil;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HttpResponse extends HttpPacket {
	//	private static Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private HttpResponseStatus status = HttpResponseStatus.C200;

	private HttpRequest httpRequest = null;

	private List<Cookie> cookies = null;
	//	private int contentLength;
	//	private byte[] bodyBytes;
	private String charset = HttpConst.CHARSET_NAME;

	/**
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 */
	public HttpResponse(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;

		String Connection = StringUtils.lowerCase(httpRequest.getHeader(HttpConst.RequestHeaderKey.Connection));
		if (StringUtils.equals(Connection, HttpConst.RequestHeaderValue.Connection.keep_alive)) {
			addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
			addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
		}

		addHeader(HttpConst.ResponseHeaderKey.Server, HttpConst.SERVER_INFO);
		//		String xx = DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis());
		//		addHeader(HttpConst.ResponseHeaderKey.Date, DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis()));
		//		addHeader(HttpConst.ResponseHeaderKey.Date, new Date().toGMTString());
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2017年2月22日 下午4:14:40
	 * 
	 */
	public static void main(String[] args) {
	}

	public boolean addCookie(Cookie cookie) {
		if (cookies == null) {
			synchronized (this) {
				if (cookies == null) {
					cookies = new ArrayList<>();
				}
			}
		}
		return cookies.add(cookie);
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body, HttpRequest httpRequest) {
		this.body = body;
		if (body != null) {
			gzip(httpRequest);
		}
	}

	private void gzip(HttpRequest httpRequest) {
		//Accept-Encoding
		//		检查浏览器是否支持gzip
		String Accept_Encoding = httpRequest.getHeaders().get(HttpConst.RequestHeaderKey.Accept_Encoding);
		if (StringUtils.isNoneBlank(Accept_Encoding)) {
			String[] ss = StringUtils.split(Accept_Encoding, ",");
			if (ArrayUtil.contains(ss, "gzip")) {
				byte[] bs = this.getBody();
				if (bs.length >= 600) {
					byte[] bs2 = ZipUtil.gzip(bs);
					if (bs2.length < bs.length) {
						this.body = bs2;
						this.addHeader(HttpConst.ResponseHeaderKey.Content_Encoding, "gzip");
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @author: tanyaowu
	 */
	//	public void removeCookie(Cookie cookie) {
	//		if (cookies == null) {
	//			return;
	//		}
	//		return cookies.add(cookie);
	//	}

	//	/**
	//	 * @return the bodyLength
	//	 */
	//	public int getContentLength()
	//	{
	//		return contentLength;
	//	}
	//
	//	/**
	//	 * @param bodyLength the bodyLength to set
	//	 */
	//	public void setContentLength(int contentLength)
	//	{
	//		this.contentLength = contentLength;
	//	}

	/**
	 * @return the status
	 */
	public HttpResponseStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}

	//	/**
	//	 * @return the bodyBytes
	//	 */
	//	public byte[] getBodyBytes() {
	//		return bodyBytes;
	//	}
	//
	//	/**
	//	 * @param bodyBytes the bodyBytes to set
	//	 */
	//	public void setBody(byte[] bodyBytes) {
	//		this.bodyBytes = bodyBytes;
	//	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return the httpRequest
	 */
	public HttpRequest getHttpRequestPacket() {
		return httpRequest;
	}

	/**
	 * @param httpRequest the httpRequest to set
	 */
	public void setHttpRequestPacket(HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	@Override
	public String logstr() {
		String str = null;
		if (httpRequest != null) {
			str = "\r\n响应: 请求ID_" + httpRequest.getId();
			str += "\r\n" + this.getHeaderString();
		} else {
			str = "\r\n响应\r\n" + status.getHeaderText();
		}
		return str;
	}

}
