package org.tio.http.server.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.MimeType;
import org.tio.http.server.HttpServerConfig;
import org.tio.json.Json;

import com.xiaoleilu.hutool.io.FileUtil;

/**
 * @author tanyaowu 
 * 2017年6月29日 下午4:17:24
 */
public class Resps {
	private static Logger log = LoggerFactory.getLogger(Resps.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public Resps() {
	}

	/**
	 * Content-Type: text/html; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse html(HttpRequest request, String bodyString, String charset, HttpServerConfig httpConfig) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_HTML_HTML.getType() + "; charset=" + charset, httpConfig);
		return ret;
	}

	/**
	 * 
	 * @param request
	 * @param bodyString
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse html(HttpRequest request, String bodyString, HttpServerConfig httpConfig) {
		return html(request, bodyString, httpConfig.getCharset(), httpConfig);
	}

	/**
	 * 根据文件创建响应
	 * @param request
	 * @param fileOnServer
	 * @param httpConfig
	 * @return
	 * @throws IOException
	 * @author: tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, File fileOnServer, HttpServerConfig httpConfig) throws IOException {
		Date lastModified = FileUtil.lastModifiedTime(fileOnServer);
		HttpResponse ret = try304(request, lastModified.getTime(), httpConfig);
		if (ret != null) {
			return ret;
		}

		byte[] bodyBytes = FileUtil.readBytes(fileOnServer);
		String filename = fileOnServer.getName();
		ret = file(request, bodyBytes, filename, httpConfig);
		ret.addHeader(HttpConst.ResponseHeaderKey.Last_Modified, lastModified.getTime() + "");
		return ret;
	}

	/**
	 * 尝试返回304
	 * @param request
	 * @param lastModifiedOnServer 服务器中资源的lastModified
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse try304(HttpRequest request, long lastModifiedOnServer, HttpServerConfig httpConfig) {
		String If_Modified_Since = request.getHeader(HttpConst.RequestHeaderKey.If_Modified_Since);//If-Modified-Since
		if (StringUtils.isNoneBlank(If_Modified_Since)) {
			Long If_Modified_Since_Date = null;
			try {
				If_Modified_Since_Date = Long.parseLong(If_Modified_Since);

				if (lastModifiedOnServer <= If_Modified_Since_Date) {
					HttpResponse ret = new HttpResponse(request, httpConfig);
					ret.setStatus(HttpResponseStatus.C304);
					return ret;
				}
			} catch (NumberFormatException e) {
				log.warn("{}, {}不是整数，浏览器信息:{}", request.getRemote(), If_Modified_Since, request.getHeader(HttpConst.RequestHeaderKey.User_Agent));
				return null;
			}
		}

		return null;
	}

	/**
	 * 根据文件创建响应
	 * @param request
	 * @param bodyBytes
	 * @param filename
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, byte[] bodyBytes, String filename, HttpServerConfig httpConfig) {
		String contentType = null;
		String extension = FilenameUtils.getExtension(filename);
		if (StringUtils.isNoneBlank(extension)) {
			MimeType mimeType = MimeType.fromExtension(extension);
			if (mimeType != null) {
				contentType = mimeType.getType();
			} else {
				contentType = "application/octet-stream";
			}
		}
		return fileWithContentType(request, bodyBytes, contentType, httpConfig);
	}

	/**
	 * 
	 * @param request
	 * @param bodyBytes
	 * @param contentType 形如:application/octet-stream等
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse fileWithContentType(HttpRequest request, byte[] bodyBytes, String contentType, HttpServerConfig httpConfig) {
		HttpResponse ret = new HttpResponse(request, httpConfig);
		ret.setBodyAndGzip(bodyBytes, request);
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, contentType);
		return ret;
	}

	/**
	 * 
	 * @param request
	 * @param bodyBytes
	 * @param headers
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse fileWithHeaders(HttpRequest request, byte[] bodyBytes, Map<String, String> headers, HttpServerConfig httpConfig) {
		HttpResponse ret = new HttpResponse(request, httpConfig);
		ret.setBodyAndGzip(bodyBytes, request);
		ret.addHeaders(headers);
		return ret;
	}

	/**
	 * Content-Type: application/json; charset=utf-8
	 * @param request
	 * @param body
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse json(HttpRequest request, Object body, String charset, HttpServerConfig httpConfig) {
		HttpResponse ret = null;
		if (body == null) {
			ret = string(request, "", charset, MimeType.TEXT_PLAIN_JSON.getType() + "; charset=" + charset, httpConfig);
		} else {
			if (body.getClass() == String.class) {
				ret = string(request, (String)body, charset, MimeType.TEXT_PLAIN_JSON.getType() + "; charset=" + charset, httpConfig);
			} else {
				ret = string(request, Json.toJson(body), charset, MimeType.TEXT_PLAIN_JSON.getType() + "; charset=" + charset, httpConfig);
			}
		}
		
		return ret;
	}

	/**
	 * Content-Type: application/json; charset=utf-8
	 * @param request
	 * @param body
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse json(HttpRequest request, Object body, HttpServerConfig httpConfig) {
		return json(request, body, httpConfig.getCharset(), httpConfig);
	}

	/**
	 * Content-Type: text/css; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse css(HttpRequest request, String bodyString, String charset, HttpServerConfig httpConfig) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_CSS_CSS.getType() + "; charset=" + charset, httpConfig);
		return ret;
	}

	/**
	 * Content-Type: text/css; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse css(HttpRequest request, String bodyString, HttpServerConfig httpConfig) {
		return css(request, bodyString, httpConfig.getCharset(), httpConfig);
	}

	/**
	 * Content-Type: application/javascript; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse js(HttpRequest request, String bodyString, String charset, HttpServerConfig httpConfig) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.APPLICATION_JAVASCRIPT_JS.getType() + "; charset=" + charset, httpConfig);
		return ret;
	}

	/**
	 * Content-Type: application/javascript; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse js(HttpRequest request, String bodyString, HttpServerConfig httpConfig) {
		return js(request, bodyString, httpConfig.getCharset(), httpConfig);
	}

	/**
	 * Content-Type: text/plain; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse txt(HttpRequest request, String bodyString, String charset, HttpServerConfig httpConfig) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_PLAIN_TXT.getType() + "; charset=" + charset, httpConfig);
		return ret;
	}

	/**
	 * Content-Type: text/plain; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse txt(HttpRequest request, String bodyString, HttpServerConfig httpConfig) {
		return txt(request, bodyString, httpConfig.getCharset(), httpConfig);
	}

	/**
	 * 创建字符串输出
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @param Content_Type
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse string(HttpRequest request, String bodyString, String charset, String Content_Type, HttpServerConfig httpConfig) {
		HttpResponse ret = new HttpResponse(request, httpConfig);
		if (bodyString != null) {
			try {
				ret.setBodyAndGzip(bodyString.getBytes(charset), request);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, Content_Type);
		return ret;
	}

	/**
	 * 创建字符串输出
	 * @param request
	 * @param bodyString
	 * @param Content_Type
	 * @param httpConfig
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse string(HttpRequest request, String bodyString, String Content_Type, HttpServerConfig httpConfig) {
		return string(request, bodyString, httpConfig.getCharset(), Content_Type, httpConfig);
	}

	/**
	 * 重定向
	 * @param request
	 * @param path
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse redirect(HttpRequest request, String path, HttpServerConfig httpConfig) {
		HttpResponse ret = new HttpResponse(request, httpConfig);
		ret.setStatus(HttpResponseStatus.C302);
		ret.addHeader(HttpConst.ResponseHeaderKey.Location, path);
		return ret;
	}

	//	　　302 （307）：与响应头location 结合完成页面重新跳转。

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
