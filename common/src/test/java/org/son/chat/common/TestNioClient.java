package org.son.chat.common;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;
import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.core.socket.impl.ServerSocket;
import org.son.chat.common.protocol.ChatHandle;
import org.son.chat.common.protocol.PackageDefaultCoder;

public class TestNioClient {

	@Test
	public void normal() {
		ICoderParserManager coderParserManager = new CoderParserManager();
		coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));

		ClientSocket.valueOf(SocketChannelConfig.valueOf(6969),coderParserManager).start();

	}

	@Test
	public void serverMode() {
		ICoderParserManager coderParserManager = new CoderParserManager();
		coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));

		final ServerSocket serverSocket = ServerSocket.valueOf(SocketChannelConfig.valueOf(8888),coderParserManager);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("registerClientSocket");
				serverSocket.registerClientSocket(SocketChannelConfig.valueOf(6969));
			}
		}, 5000);

		serverSocket.start();
	}
}
