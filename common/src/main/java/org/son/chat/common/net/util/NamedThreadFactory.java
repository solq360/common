package org.son.chat.common.net.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名线程
 * @author solq
 */
public class NamedThreadFactory implements ThreadFactory {

	private final String name;
	private final AtomicInteger count = new AtomicInteger();

	public NamedThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		final String name = this.name + ":" + count.getAndIncrement();
		return new Thread(r, name);
	}

}
