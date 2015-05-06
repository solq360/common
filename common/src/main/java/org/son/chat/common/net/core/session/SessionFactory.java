package org.son.chat.common.net.core.session;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 会话工厂
 * 
 * @author solq
 * */
public class SessionFactory implements ISessionFactory {

    private final static AtomicLong NUM = new AtomicLong();

    @Override
    public ISession createSession() {
	Session result = Session.valueOf(buildId());
	return result;
    }

    @Override
    public void destroySession(ISession session) {

    }

    private static String buildId() {
	long value = NUM.getAndIncrement();
	if (value > 10000000L) {
	    synchronized (NUM) {
		if (NUM.getAndIncrement() > 10000000L) {
		    NUM.set(0);
		}
	    }
	}
	return String.valueOf(value);
    }
}
