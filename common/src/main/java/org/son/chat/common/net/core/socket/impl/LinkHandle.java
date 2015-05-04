package org.son.chat.common.net.core.socket.impl;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.socket.ISocketHandle;

public class LinkHandle implements ISocketHandle {
    private ClientPipeChannel channelClients;

    public LinkHandle(ClientPipeChannel channelClients) {
	this.channelClients = channelClients;
    }

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
