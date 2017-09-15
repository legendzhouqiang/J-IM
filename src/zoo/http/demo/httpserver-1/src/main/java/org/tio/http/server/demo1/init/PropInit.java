package org.tio.http.server.demo1.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.PropKit;

/**
 * @author tanyaowu
 * 2017年8月7日 下午1:49:05
 */
public class PropInit {
	private static Logger log = LoggerFactory.getLogger(PropInit.class);

	public static void init() {
		try {
			PropKit.use("app.properties");
		} catch (Exception e2) {
			log.error(e2.toString(), e2);
		}
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public PropInit() {
	}
}
