package org.son.chat.common.net.core.session;

/**
 * 会话对象管理
 * 
 * @author solq
 * */
public class SessionFactory implements ISessionFactory {

    public ISession createSession() {
	Session result = Session.valueOf("1");
	return result;
    }

    @Override
    public void destroySession(ISession session) {
	
    }
}
