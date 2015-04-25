package org.son.chat.common.net.core;

import java.nio.channels.spi.AbstractSelectableChannel;

import org.son.chat.common.net.handle.ISocketHandle;

/**
 * socketchannel 模板
 * @author solq
 */
public abstract class AbstractISocketChannel implements ISocketChannel {

	protected ICoderParser coderParser;
	protected AbstractSelectableChannel channel;
	protected ISocketHandle socketHandle;

	protected SocketChannelConfig socketChannelConfig;

	@Override
	public SocketChannelConfig getSocketChannelConfig() {
		return socketChannelConfig;
	}

	@Override
	public void open() {
		socketHandle.open(channel);
	}

	@Override
	public void register() {
		socketHandle.register(channel);
	}

	@Override
	public void close() {
		socketHandle.close(channel);
	}

	@Override
	public void unRegister() {
		socketHandle.unRegister(channel);
	}

}
