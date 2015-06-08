package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

import org.son.chat.common.net.core.socket.ISocketChannel;
import org.son.chat.common.net.core.socket.ISocketPool;

/**
 * 单线程,参考NETTY 单线程组串行处理
 * 
 * @author solq
 */
public class SocketPool implements ISocketPool, Runnable {

    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD = 512;
    private Selector selector;
    private Thread thread;
    private LinkedList<Runnable> queue = new LinkedList<>();
    private ReentrantLock lock = new ReentrantLock();

    private ISocketChannel serverChannel;

    private String name;
    /** 执行标志 */
    private boolean run = false;
    private boolean init = false;

    private long lastExecutionTime;
    /** 每提交个任务，触发 {@link Selector#selectNow()} */
    private boolean needsToSelectAgain;

    public SocketPool(String name, ISocketChannel serverChannel) {
	this.serverChannel = serverChannel;
	this.name = name;
	try {
	    selector = Selector.open();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void execute(Runnable task) {
	if (!run) {
	    return;
	}
	lock.lock();
	try {
	    queue.add(task);
	} finally {
	    lock.unlock();
	}
	if (needsToSelectAgain) {
	    needsToSelectAgain = false;
	    try {
		selector.selectNow();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    private Runnable take() {
	lock.lock();
	try {
	    if (queue.size() == 0) {
		return null;
	    }
	    return queue.removeFirst();
	} finally {
	    lock.unlock();
	}
    }

    private void processNio() {
	select();
	processKeys();
    }

    private void processKeys() {
	for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
	    // 得到下一个Key
	    SelectionKey key = i.next();
	    try {
		handle(key);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    i.remove();
	}
    }

    /**
     * 参考 netty : NioEventLoop
     * */
    @Override
    public void select() {
	// select
	// http://goldendoc.iteye.com/blog/1152079
	// http://www.iteye.com/topic/650195

	try {
	    int selectCnt = 0;
	    long currentTimeNanos = System.nanoTime();
	    long selectDeadLineNanos = currentTimeNanos+1000000L*1000;
	    for (;;) {
		long timeoutMillis = (selectDeadLineNanos-currentTimeNanos + 500000L) / 1000000L;
		if (timeoutMillis <= 0) {
		    if (selectCnt == 0) {
			//System.out.println("timeoutMillis : " +timeoutMillis + " name :" +this.name + " selectCnt :"  + selectCnt);

			selector.selectNow();
			selectCnt = 1;
		    }
		    break;
		}
		//System.out.println("timeoutMillis : " +timeoutMillis + " name :" +this.name + " selectCnt :"  + selectCnt);
		int selectedKeys = selector.select(timeoutMillis);
		selectCnt++;
		// || oldWakenUp || wakenUp.get() || hasTasks()
		if (selectedKeys != 0 || taskCount()>0) {
 		    break;
		}

		// cpu 100%处理
		if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {

		    // rebuildSelector();
		    // selector = this.selector;
		    //
		    // // Select again to populate selectedKeys.
		    // selector.selectNow();
		    // selectCnt = 1;
		    // break;
		    System.out.println("selectCnt 超过 SELECTOR_AUTO_REBUILD_THRESHOLD");
		}

		currentTimeNanos = System.nanoTime();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    private void handle(SelectionKey key) {
	ISocketChannel channel = (ISocketChannel) key.attachment();
	if (!key.isValid()) {
	    System.out.println("error key");
	    if (channel != null) {
		channel.doClose(key);
	    }
	    return;
	}
	// 可能存在一次在channel 注册多个操作 不能用 elseif
	try {
	    if (key.isAcceptable()) {
		serverChannel.doAccept(key);
	    }
	    if (key.isConnectable()) {
		if (serverChannel != null) {
		    serverChannel.doConnect(key);
		} else {
		    channel.doConnect(key);
		}
	    }
	    if (key.isReadable()) {
		if (serverChannel != null) {
		    serverChannel.doRead(key);
		} else {
		    channel.doRead(key);
		}
	    }
	    // 不清楚直接 isWritable 会抛异常
	    if (key.isValid() && key.isWritable()) {
		if (serverChannel != null) {
		    serverChannel.doWrite(key);
		} else {
		    channel.doWrite(key);
		}
	    }
	} catch (CancelledKeyException e) { // key 消除事件
	    e.printStackTrace();
	    if (channel != null) {
		channel.doClose(key);
	    }
	}
    }

    private void processTask(long timeoutNanos) {
	//System.out.println("processTask");

	// 处理任务计算器
	int count = 0;
	final long deadline = System.nanoTime() + timeoutNanos;
	while (true) {
	    try {
		Runnable task = take();
		if (task == null) {
		    break;
		}
		task.run();
	    } catch (Exception e) {
		// logger.error("同步队列[" + key + "]处理线程出现未知错误", e);
	    }
	    // 延时退出
	    if ((count++ % 255) == 0) {
		if (System.nanoTime() > deadline) {
		    break;
		}
	    }
	}
	this.lastExecutionTime = System.nanoTime();
    }

    /***
     * 参考 netty : NioEventLoop
     * */
    @Override
    public void run() {
	while (true) {

	    /**
	     * 优先级处理io任务 -> 非IO任务 -> 停服处理 <br>
	     * 1.先判断是否有非IO任务 ? 马上映醒selectNow() : 阻塞 线程 <br>
	     * 2.当连继出现 select(timeOut) ==0 超过512默认边界即认为出现 epoll的cpu 100% bug 进行
	     * rebuild selector 处理<br>
	     * 3.计算执行IO任务时间，然后按比例预计非 io任务执行时间<br>
	     * 4.非io任务执行次数超过64 并且超过执行时间退出 <br>
	     * */
	    final long ioStartTime = System.nanoTime();
	    processNio();
	    needsToSelectAgain = false;

	    final long ioTime = System.nanoTime() - ioStartTime;
	    final int ioRatio = 50;
	    // task 执行时间占nio 时间比
	    final long taskTime = ioTime * (100 - ioRatio) / ioRatio;
	    processTask(taskTime);
	    // 判断关闭退出
	    if (!run) {
		queue.clear();
		break;
	    }

	}
    }

    @Override
    public void shutdown() {
	run = false;
    }

    @Override
    public Selector getSelector() {
	return selector;
    }

    @Override
    public int taskCount() {
	return queue.size();
    }

    @Override
    public boolean isRun() {
	return run;
    }

    @Override
    public void init() {
	if (this.init) {
	    return;
	}
	synchronized (this) {
	    if (this.init) {
		return;
	    }
	    this.init = true;
	    run = true;
	    thread = new Thread(this, name);
	    thread.setDaemon(true);
	    thread.start();
	}
    }

}
