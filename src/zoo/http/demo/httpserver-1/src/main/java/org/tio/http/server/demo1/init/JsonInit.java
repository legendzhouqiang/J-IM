package org.tio.http.server.demo1.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.json.Json;
import org.tio.utils.jfinal.JfinalRecordSerializer;

import com.jfinal.plugin.activerecord.Record;

/**
 * @author tanyaowu
 * 2017年8月18日 下午4:52:28
 */
public class JsonInit {
	private static Logger log = LoggerFactory.getLogger(JsonInit.class);

	public static void init() {
		Json.put(Record.class, JfinalRecordSerializer.instance);
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
	public JsonInit() {
	}
}
