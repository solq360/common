package org.son.chat.common;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.socket.impl.ClientSocket;

public class TestNioClient {

	public void testClient() {
		ClientSocket.valueOf(SocketChannelConfig.valueOf(6969)).start();
	}

	public static void main(String[] args) {
		new TestNioClient().testClient();
	}
}
