package org.son.chat.common.net.handle;

import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;

/**
 * socket 链路逻辑处理
 * @author solq
 */
public interface ISocketHandle {

	void open(SocketChannelCtx ctx);

	void register(SocketChannelCtx ctx);

	void close(SocketChannelCtx ctx);

	void unRegister(SocketChannelCtx ctx);

}
