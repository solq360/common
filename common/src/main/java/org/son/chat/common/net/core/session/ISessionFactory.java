package org.son.chat.common.net.core.session;

/**
 * 会话对象管理
 * 
 * @author solq
 * */
public interface ISessionFactory {
    public ISession createSession();

    public void destroySession(ISession session);
}
