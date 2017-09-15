package org.tio.executor;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Copyright (c) for darkidiot
 * Date:2017/8/10
 * Author: <a href="darkidiot@icloud.com">darkidiot</a>
 * Desc: 线程工厂类
 */
public class FlyweightThreadFactory implements ThreadFactory {

	/** The thread factory name. */
    private String name;

	/** The map of name and thread factory. */
	private static Map<String, FlyweightThreadFactory> mapOfNameAndThreadFactory = Maps.newHashMap();

	/** The map of name and atomic integer. */
	private static Map<String, AtomicInteger> mapOfNameAndAtomicInteger = Maps.newHashMap();

	/** The priority. */
	private int priority = Thread.NORM_PRIORITY;

	/**
	 * Gets the single instance of FlyweightThreadFactory.
	 *
	 * @param name the thread name
	 * @param priority the priority
	 * @return single instance of FlyweightThreadFactory
	 */
	public static FlyweightThreadFactory getInstance(String name, Integer priority) {
		FlyweightThreadFactory defaultThreadFactory = mapOfNameAndThreadFactory.get(name);
		if (defaultThreadFactory == null) {
			defaultThreadFactory = new FlyweightThreadFactory();
			if (priority != null) {
				defaultThreadFactory.priority = priority;
			}

			defaultThreadFactory.setName(name);
			mapOfNameAndThreadFactory.put(name, defaultThreadFactory);
			mapOfNameAndAtomicInteger.put(name, new AtomicInteger());
		}
		return defaultThreadFactory;
	}

	public static FlyweightThreadFactory newInstance(String name) {
		return getInstance(name, Thread.NORM_PRIORITY);
	}

	/**
	 * Instantiates a new default thread factory.
	 */
	private FlyweightThreadFactory() {
	}

	/** 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName(String.format("%s-%d",this.getName(),mapOfNameAndAtomicInteger.get(this.getName()).incrementAndGet()));
		thread.setPriority(priority);
		return thread;
	}

	/**
	 * Get the thread factory name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the thread factory name.
	 */
	public void setName(String name) {
		this.name = name;
	}

}