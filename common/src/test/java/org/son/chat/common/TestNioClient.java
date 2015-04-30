package org.son.chat.common;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.socket.impl.ClientSocket;

public class TestNioClient {

	public static void main(String[] args) {
		ClientSocket.valueOf(SocketChannelConfig.valueOfRemote(6969)).start();
	}
}
