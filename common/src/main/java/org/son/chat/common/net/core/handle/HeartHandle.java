package org.son.chat.common.net.core.handle;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.session.Key;
import org.son.chat.common.net.core.session.SessionKey;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.util.NamedThreadFactory;

/**
 * 心跳管理
 * 
 * @author solq
 * */
public class HeartHandle extends AbstractSocketHandle {

    private final static ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("Heart"));

    private long delayTime;

    public HeartHandle(long delayTime) {
	this.delayTime = delayTime;
    }

    @Override
    public void openAfter(ICoderCtx ctx) {
	record(ctx, SessionKey.KEY_CONNECT_TIME);
    }

    @Override
    public void readAfter(ICoderCtx ctx, Object request) {
	record(ctx, SessionKey.KEY_READ_TIME);
    }

    @Override
    public void writeAfter(ICoderCtx ctx, Object response) {
	record(ctx, SessionKey.KEY_SEND_TIME);
    }

    private void record(ICoderCtx ctx, Key<Date> key) {
	final Date now = new Date();
	ISession session = getSession(ctx);
	key.setAttr(session, now);
	SessionKey.KEY_HEART_TIME.setAttr(session, now);

	Future<?> preTask = SessionKey.KEY_HEART_TASK.getAttr(session);
	if (preTask != null) {
	    return;
	}

	synchronized (session) {
	    preTask = SessionKey.KEY_HEART_TASK.getAttr(session);
	    if (preTask != null) {
		return;
	    }
	    final long endTime = System.currentTimeMillis() + delayTime;
	    final HeartTask task = new HeartTask(ctx, endTime);
	    Future<?> future = scheduled.schedule(task, delayTime, TimeUnit.MILLISECONDS);
	    SessionKey.KEY_HEART_TASK.setAttr(session, future);
	}
    }

    private final class HeartTask implements Runnable {
	private ICoderCtx ctx;
	private long endTime;

	public HeartTask(ICoderCtx ctx, long endTime) {
	    this.ctx = ctx;
	    this.endTime = endTime;
	}

	@Override
	public void run() {
	    final ISession session = getSession(ctx);
	    final Date time = SessionKey.KEY_HEART_TIME.getAttr(session);
	    final ClientSocket clientSocket = getClientSocket(ctx);
	    if (clientSocket.isClose()) {
		return;
	    }
	    // close
	    if (time == null || time.getTime() < this.endTime) {
 		clientSocket.stop();
		return;
	    }
	    // 下次触发时间-当前时间 = 延时执行偏移时间
	    long delay = time.getTime() + getDelayTime() - System.currentTimeMillis();
	    if (delay <= 0) { 
		clientSocket.stop();
		return;
	    }
	    this.endTime = System.currentTimeMillis() + delay;
	    Future<?> future = scheduled.schedule(new HeartTask(ctx, endTime), delay, TimeUnit.MILLISECONDS);
	    SessionKey.KEY_HEART_TASK.setAttr(session, future);
	}
    }

    // getter
    public long getDelayTime() {
	return delayTime;
    }
}
