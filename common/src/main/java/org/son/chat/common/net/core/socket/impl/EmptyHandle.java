package org.son.chat.common.net.core.socket.impl;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.socket.ISocketHandle;

public class EmptyHandle implements ISocketHandle {

    @Override
    public void open(ICoderCtx ctx) {

    }

    @Override
    public void register(ICoderCtx ctx) {

    }

    @Override
    public void close(ICoderCtx ctx) {

    }

    @Override
    public void unRegister(ICoderCtx ctx) {

    }
}
