package org.tio.http.server.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.MimeType;

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
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse html(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_HTML_HTML.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * 根据文件创建响应
	 * @param request
	 * @param fileOnServer
	 * @return
	 * @throws IOException
	 * @author: tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, File fileOnServer) throws IOException {
		Date lastModified = FileUtil.lastModifiedTime(fileOnServer);
		
		String If_Modified_Since = request.getHeader(HttpConst.RequestHeaderKey.If_Modified_Since);//If-Modified-Since
		if (StringUtils.isNoneBlank(If_Modified_Since)) {
			Long If_Modified_Since_Date = null;
			try {
//				If_Modified_Since_Date = DatePattern.NORM_DATETIME_MS_FORMAT.parse(If_Modified_Since);
				If_Modified_Since_Date = Long.parseLong(If_Modified_Since);
			} catch (NumberFormatException e) {
				log.warn("{}不是整数", If_Modified_Since);
			}
			
			if (If_Modified_Since_Date != null) {
				long lastModifiedTime = Long.MAX_VALUE;
				try {
					//此处这样写是为了保持粒度一致，否则可能会判断失误
					lastModifiedTime = lastModified.getTime();
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
//				long If_Modified_Since_Date_Time = If_Modified_Since_Date.getTime();
				if (lastModifiedTime <= If_Modified_Since_Date) {
					HttpResponse ret = new HttpResponse(request);
					ret.setStatus(HttpResponseStatus.C304);
					return ret;
				}
			}
		}
		
		byte[] bodyBytes = FileUtil.readBytes(fileOnServer);
		String filename = fileOnServer.getName();
		HttpResponse ret = file(request, bodyBytes, filename);
		ret.addHeader(HttpConst.ResponseHeaderKey.Last_Modified,  lastModified.getTime() + "");
		return ret;
	}

	/**
	 * 根据文件创建响应
	 * @param request
	 * @param bodyBytes
	 * @param filename
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse file(HttpRequest request, byte[] bodyBytes, String filename) {
		HttpResponse ret = new HttpResponse(request);
		ret.setBody(bodyBytes, request);

		String mimeTypeStr = null;
		String extension = FilenameUtils.getExtension(filename);
		if (StringUtils.isNoneBlank(extension)) {
			MimeType mimeType = MimeType.fromExtension(extension);
			if (mimeType != null) {
				mimeTypeStr = mimeType.getType();
			} else {
				mimeTypeStr = "application/octet-stream";
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, mimeTypeStr);
		//		ret.addHeader(HttpConst.ResponseHeaderKey.Content_disposition, "attachment;filename=\"" + filename + "\"");
		return ret;
	}

	/**
	 * Content-Type: application/json; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse json(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_PLAIN_JSON.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: text/css; charset=utf-8
	 * @param request
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse css(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_CSS_CSS.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: application/javascript; charset=utf-8
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse js(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.APPLICATION_JAVASCRIPT_JS.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * Content-Type: text/plain; charset=utf-8
	 * @param bodyString
	 * @param charset
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse txt(HttpRequest request, String bodyString, String charset) {
		HttpResponse ret = string(request, bodyString, charset, MimeType.TEXT_PLAIN_TXT.getType() + "; charset=" + charset);
		return ret;
	}

	/**
	 * 创建字符串输出
	 * @param bodyString
	 * @param charset
	 * @param Content_Type
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse string(HttpRequest request, String bodyString, String charset, String Content_Type) {
		HttpResponse ret = new HttpResponse(request);
		if (bodyString != null) {
			try {
				ret.setBody(bodyString.getBytes(charset), request);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		ret.addHeader(HttpConst.ResponseHeaderKey.Content_Type, Content_Type);
		return ret;
	}
	
	/**
	 * 重定向
	 * @param request
	 * @param path
	 * @return
	 * @author: tanyaowu
	 */
	public static HttpResponse redirect(HttpRequest request, String path) {
		HttpResponse ret = new HttpResponse(request);
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
