package org.son.chat.common.net.handle;

import java.nio.channels.spi.AbstractSelectableChannel;

/**
 * socket 链路逻辑处理
 * @author solq
 */
public interface ISocketHandle {

	void open(AbstractSelectableChannel channel);

	void register(AbstractSelectableChannel channel);

	void close(AbstractSelectableChannel channel);

	void unRegister(AbstractSelectableChannel channel);

}
