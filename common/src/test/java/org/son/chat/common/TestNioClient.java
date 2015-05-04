package org.son.chat.common;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;
import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.handle.EmptyHandle;
import org.son.chat.common.net.core.session.ISessionFactory;
import org.son.chat.common.net.core.session.SessionFactory;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.core.socket.impl.ServerSocket;
import org.son.chat.common.protocol.ChatHandle;
import org.son.chat.common.protocol.PackageDefaultCoder;

public class TestNioClient {

    @Test
    public void normal() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	ClientSocket.valueOf(SocketChannelConfig.valueOf(6969), coderParserManager, new EmptyHandle()).start();
    }

    @Test
    public void serverMode() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	ISessionFactory sessionFactory = new SessionFactory();
	final ServerSocket serverSocket = ServerSocket.valueOf(SocketChannelConfig.valueOf(8888), coderParserManager,sessionFactory);

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
