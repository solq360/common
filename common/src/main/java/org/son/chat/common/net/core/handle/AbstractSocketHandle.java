package org.son.chat.common.net.core.handle;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;

/**
 * @author solq
 * */
public abstract class AbstractSocketHandle implements ISocketHandle {
    
    protected static ClientSocket getClientSocket(ICoderCtx ctx){
	return ((SocketChannelCtx) ctx).getClientSocket();
    }
    
    protected static ISession getSession(ICoderCtx ctx){
	return getClientSocket(ctx).getSession();
    }
    
    @Override
    public void openBefore(ICoderCtx ctx) {

    }

    @Override
    public void openAfter(ICoderCtx ctx) {

    }

    @Override
    public void closeBefore(ICoderCtx ctx) {

    }

    @Override
    public void closeAfter(ICoderCtx ctx) {

    }

    @Override
    public void readBefore(ICoderCtx ctx, Object request) {

    }

    @Override
    public void readAfter(ICoderCtx ctx, Object request) {

    }

    @Override
    public void writeBefore(ICoderCtx ctx, Object response) {

    }

    @Override
    public void writeAfter(ICoderCtx ctx, Object response) {

    }

    @Override
    public void openError(ICoderCtx ctx) {

    }

    @Override
    public void writeError(ICoderCtx ctx, Object response) {

    }
}
