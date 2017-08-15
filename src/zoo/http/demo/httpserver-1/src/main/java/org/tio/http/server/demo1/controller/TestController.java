package org.tio.http.server.demo1.controller;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.UploadFile;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.annotation.RequestPath;
import org.tio.http.server.demo1.model.User;
import org.tio.http.server.session.HttpSession;
import org.tio.http.server.util.Resps;
import org.tio.json.Json;

/**
 * @author tanyaowu 
 * 2017年6月29日 下午7:53:59
 */
@RequestPath(value = "/test")
public class TestController {
	private static Logger log = LoggerFactory.getLogger(TestController.class);

	String html = "<div style='position:relation;border-radius:10px;text-align:center;padding:10px;font-size:40pt;font-weight:bold;background-color:##e4eaf4;color:#2d8cf0;border:0px solid #2d8cf0; width:600px;height:400px;margin:auto;box-shadow: 1px 1px 50px #000;position: fixed;top:0;left:0;right:0;bottom:0;'>"
			+ "<a style='text-decoration:none' href='https://git.oschina.net/tywo45/t-io' target='_blank'>"
			+ "<div style='text-shadow: 8px 8px 8px #99e;'>hello tio httpserver</div>" + "</a>" + "</div>";

	String txt = html;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public TestController() {
	}

	@RequestPath(value = "/putsession")
	public HttpResponse putsession(String value, HttpRequest httpRequest, HttpServerConfig config, HttpSession httpSession, ChannelContext channelContext)
			throws Exception {
		httpSession.setAtrribute("test", value);
		HttpResponse ret = Resps.json(httpRequest, "设置成功:" + value, config);
		return ret;
	}

	@RequestPath(value = "/getsession")
	public HttpResponse getsession(HttpRequest httpRequest, HttpServerConfig config, HttpSession httpSession, ChannelContext channelContext) throws Exception {
		String value = (String) httpSession.getAtrribute("test");
		HttpResponse ret = Resps.json(httpRequest, "获取的值:" + value, config);
		return ret;
	}

	@RequestPath(value = "/json")
	public HttpResponse json(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.json(httpRequest, "{\"ret\":\"OK\"}", config);
		return ret;
	}

	@RequestPath(value = "/txt")
	public HttpResponse txt(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.txt(httpRequest, txt, config);
		return ret;
	}

	@RequestPath(value = "/html")
	public HttpResponse html(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.html(httpRequest, html, config);
		return ret;
	}

	@RequestPath(value = "/abtest")
	public HttpResponse abtest(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.html(httpRequest, "OK", config);
		return ret;
	}

	/**
	 * 测试映射重复
	 */
	@RequestPath(value = "/abtest")
	public HttpResponse abtest1(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		log.info("");
		HttpResponse ret = Resps.html(httpRequest, "OK---------1", config);
		return ret;
	}

	@RequestPath(value = "/filetest")
	public HttpResponse filetest(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.file(httpRequest, new File("d:/tio.exe"), config);
		return ret;
	}

	@RequestPath(value = "/filetest.zip")
	public HttpResponse filetest_zip(HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.file(httpRequest, new File("d:/eclipse-jee-neon-R-win32-x86_64.zip"), config);
		return ret;
	}

	/**
	 * 上传文件测试
	 * @param uploadFile
	 * @param httpRequest
	 * @param config
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@RequestPath(value = "/upload")
	public HttpResponse upload(UploadFile uploadFile, String before, String end, HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext)
			throws Exception {
		HttpResponse ret;
		if (uploadFile != null) {
			File file = new File("c:/" + uploadFile.getName());
			FileUtils.writeByteArrayToFile(file, uploadFile.getData());

			System.out.println("【" + before + "】");
			System.out.println("【" + end + "】");

			ret = Resps.html(httpRequest, "文件【" + uploadFile.getName() + "】【" + uploadFile.getSize() + "字节】上传成功", config);
		} else {
			ret = Resps.html(httpRequest, "请选择文件再上传", config);
		}
		return ret;
	}

	@RequestPath(value = "/post")
	public HttpResponse post(String before, String end, HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.html(httpRequest, "before:" + before + "<br>end:" + end, config);
		return ret;

	}

	@RequestPath(value = "/plain")
	public HttpResponse plain(String before, String end, HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		String bodyString = httpRequest.getBodyString();
		HttpResponse ret = Resps.html(httpRequest, bodyString, config);
		return ret;
	}

	@RequestPath(value = "/bean")
	public HttpResponse bean(User user, HttpRequest httpRequest, HttpServerConfig config, ChannelContext channelContext) throws Exception {
		HttpResponse ret = Resps.json(httpRequest, Json.toFormatedJson(user), config);
		return ret;
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args) {

	}
}
