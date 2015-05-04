package org.son.chat.common.net.core.handle;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.session.SessionFactory;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;

/**
 * 会话处理
 * 
 * @author solq
 * */
public class SessionHandle extends AbstractSocketHandle {

    private SessionFactory sessionFactory;

    public SessionHandle(SessionFactory sessionFactory) {
	this.sessionFactory = sessionFactory;
    }

    @Override
    public void openAfter(ICoderCtx ctx) {
	if (!(ctx instanceof SocketChannelCtx)) {
	    return;
	}
	ClientSocket clientSocket = ((SocketChannelCtx) ctx).getClientSocket();
	clientSocket.bindSession(sessionFactory.createSession());
    }
}
