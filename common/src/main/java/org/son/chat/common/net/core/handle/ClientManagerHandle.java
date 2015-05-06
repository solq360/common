package org.son.chat.common.net.core.handle;

import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.socket.impl.ClientPipeChannel;

/**
 * @author solq
 * */
public class ClientManagerHandle extends AbstractSocketHandle {
    private ClientPipeChannel channelClients;

    public ClientManagerHandle(ClientPipeChannel channelClients) {
	this.channelClients = channelClients;
    }

    @Override
    public void openAfter(ICoderCtx ctx) {
	this.channelClients.join(ClientPipeChannel.DEFAULT_CLIENT_CHANNEL, getClientSocket(ctx));
    }

    @Override
    public void closeAfter(ICoderCtx ctx) {
	this.channelClients.eixt(getClientSocket(ctx));
    }
}
