package org.son.chat.common.net.core.socket.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.son.chat.common.net.core.socket.ISocketPool;
import org.son.chat.common.net.util.NamedThreadFactory;

/**
 * @author solq
 */
public class SocketPool implements ISocketPool {

 	private ExecutorService pool ;
	public SocketPool (String name){
 		this.pool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
	}
	@Override
	public void execute(Runnable task) {
		pool.execute(task);
	}
}
