package org.son.chat.common.net.util;

import org.son.chat.common.net.core.socket.impl.SocketPool;

/**
 * @author solq
 */
public abstract class SocketPoolFactory {
	private final static SocketPool socketPool = new SocketPool("CHANNEL WRITE");

	public static SocketPool getInstance() {
		return socketPool;
	}
}
