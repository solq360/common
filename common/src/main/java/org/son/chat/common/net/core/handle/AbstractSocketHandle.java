package org.son.chat.common.net.core.handle;

import org.son.chat.common.net.core.coder.ICoderCtx;

/**
 * @author solq
 * */
public abstract class AbstractSocketHandle implements ISocketHandle {
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
    public void writeBefore(ICoderCtx ctx, Object reponse) {

    }

    @Override
    public void writeAfter(ICoderCtx ctx, Object reponse) {

    }

    @Override
    public void openError(ICoderCtx ctx) {

    }

    @Override
    public void writeError(ICoderCtx ctx, Object response) {

    }
}
