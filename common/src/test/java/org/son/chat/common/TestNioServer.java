package org.son.chat.common;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.socket.impl.ServerSocket;

/**
 * @author solq
 */
public class TestNioServer {

	public static void main(String[] args) {
		ServerSocket.valueOf(SocketChannelConfig.valueOfLocal(6969)).start();

	}
}
