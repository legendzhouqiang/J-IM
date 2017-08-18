package org.tio.http.server.watche;

import java.nio.file.Paths;
import java.nio.file.WatchEvent;

import com.xiaoleilu.hutool.io.watch.WatchMonitor;
import com.xiaoleilu.hutool.io.watch.Watcher;

/**
 *
 * @author tanyaowu
 * 2017年8月1日 下午3:17:54
 */
public class SimpleWatcher implements Watcher {

	public static void main(String[] args) {
		WatchMonitor watchMonitor = WatchMonitor.create(Paths.get("c://"));
		SimpleWatcher simpleWatcher = new SimpleWatcher();
		watchMonitor.setWatcher(simpleWatcher);
		watchMonitor.start();
	}

	@Override
	public void onCreate(WatchEvent<?> event) {
		Object obj = event.context();
		System.out.println("创建：" + obj);

	}

	@Override
	public void onDelete(WatchEvent<?> event) {
		Object obj = event.context();
		System.out.println("删除：" + obj);
	}

	@Override
	public void onModify(WatchEvent<?> event) {
		Object obj = event.context();
		System.out.println("修改：" + obj);
	}

	@Override
	public void onOverflow(WatchEvent<?> event) {
		Object obj = event.context();
		System.out.println("Overflow：" + obj);
	}
}
