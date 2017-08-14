package org.tio.http.server.demo1.init;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.server.HttpServerConfig;
import org.tio.http.server.HttpServerStarter;
import org.tio.http.server.demo1.HttpServerDemoStarter;
import org.tio.http.server.handler.IHttpRequestHandler;
import org.tio.utils.SystemTimer;

import com.jfinal.kit.PropKit;

/**
 * @author tanyaowu 
 * 2017年7月19日 下午4:59:04
 */
public class HttpServerInit {
	private static Logger log = LoggerFactory.getLogger(HttpServerInit.class);

	public static HttpServerConfig httpServerConfig;

	public static IHttpRequestHandler httpRequestHandler;

	public static HttpServerStarter httpServerStarter;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public HttpServerInit() {
	}

	public static void init() throws Exception {
		long start = SystemTimer.currentTimeMillis();

		PropKit.use("app.properties");

		int port = PropKit.getInt("http.port");//启动端口
		String pageRoot = PropKit.get("page.root");//html/css/js等的根目录，支持classpath:，也支持绝对路径
		String[] scanPackages = new String[] { HttpServerDemoStarter.class.getPackage().getName() };//tio mvc需要扫描的根目录包
		httpServerStarter = new HttpServerStarter(pageRoot, port, scanPackages, null);
		httpServerStarter.start();

		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		log.info("Tio Http Server启动完毕,耗时:{}ms,访问地址:http://127.0.0.1:{}", iv, port);
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	}
}
