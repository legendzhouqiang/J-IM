package org.tio.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tanyaowu
 * 2017年8月13日 上午11:36:12
 */
public class SystemTimer {
	private static class TimerTask implements Runnable {
		@Override
		public void run() {
			time = System.currentTimeMillis();
		}
	}

	private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "SystemTimer");
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final long period = Long.parseLong(System.getProperty("system.timer.period", "10"));

	private static volatile long time = System.currentTimeMillis();

	static {
		executor.scheduleAtFixedRate(new TimerTask(), period, period, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executor.shutdown();
			}
		});
	}

	/**
	 * Current time millis.
	 *
	 * @return the long
	 */
	public static long currentTimeMillis() {
		return time;
	}
}
