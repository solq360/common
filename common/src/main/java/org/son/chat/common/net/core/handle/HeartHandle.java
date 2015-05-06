package org.son.chat.common.net.core.handle;

import java.util.Date;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.session.Key;
import org.son.chat.common.net.core.session.SessionKey;
import org.son.chat.common.net.util.NamedThreadFactory;

/**
 * 心跳管理
 * 
 * @author solq
 * */
public class HeartHandle extends AbstractSocketHandle {

    private final static ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("Heart"));

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

	final Future<?> preTask = SessionKey.KEY_HEART_TASK.getAttr(session);
	if (preTask != null && !preTask.isCancelled()) {
	    preTask.cancel(true);
	}
	final HeartTask task = new HeartTask();
	final long delayTime = 2000;
	Future<?> future = scheduled.schedule(task, delayTime, TimeUnit.MILLISECONDS);
	SessionKey.KEY_HEART_TASK.setAttr(session, future);
    }

    private final static class HeartTask implements Runnable {

	@Override
	public void run() {
	    System.out.println(" HeartTask =========== : ");
	}
    }

}
