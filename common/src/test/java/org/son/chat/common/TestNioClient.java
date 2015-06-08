package org.son.chat.common;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;
import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.handle.EmptyHandle;
import org.son.chat.common.net.core.handle.HeartHandle;
import org.son.chat.common.net.core.handle.SessionHandle;
import org.son.chat.common.net.core.session.ISessionFactory;
import org.son.chat.common.net.core.session.SessionFactory;
import org.son.chat.common.net.core.session.SessionKey;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.net.core.socket.impl.ServerSocket;
import org.son.chat.common.net.core.socket.impl.SocketPool;
import org.son.chat.common.net.util.IpUtil;
import org.son.chat.common.protocol.ChatHandle;
import org.son.chat.common.protocol.PackageDefaultCoder;

public class TestNioClient {

    @Test
    public void normal() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	final ClientSocket clientSocket = ClientSocket.valueOf(SocketChannelConfig.valueOf(6969), new SocketPool("client", null), coderParserManager, new EmptyHandle());

	Timer timer = new Timer();
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {
		clientSocket.send("连接服务器成功");
		System.out.println("send ");
		this.cancel();
	    }
	}, 1000);

	clientSocket.start();
	clientSocket.sync();
	clientSocket.stop();
    }

    @Test
    public void serverMode() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	ISessionFactory sessionFactory = new SessionFactory();
	final ServerSocket serverSocket = ServerSocket.valueOf(SocketChannelConfig.valueOf(8888), 10, 20, coderParserManager, sessionFactory);

	Timer timer = new Timer();
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {
		System.out.println("registerClientSocket");
		ClientSocket clientSocket = serverSocket.registerClient(SocketChannelConfig.valueOf(6969));
		clientSocket.send("连接服务器成功");
		this.cancel();
	    }
	}, 1000);

	serverSocket.start();
	serverSocket.sync();
	serverSocket.stop();
    }

    @Test
    public void testGetIp() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	ISessionFactory sessionFactory = new SessionFactory();
	final ServerSocket serverSocket = ServerSocket.valueOf(SocketChannelConfig.valueOf(8888), 10, 20, coderParserManager, sessionFactory);

	Timer timer = new Timer();
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {
		System.out.println("registerClientSocket");
		ClientSocket clientSocket = serverSocket.registerClient(SocketChannelConfig.valueOf(6969));

		clientSocket.stop();
		System.out.println(" ip : " + IpUtil.getAddress(clientSocket.getRemoteAddress()));
		this.cancel();
	    }
	}, 1000);

	serverSocket.start();
	serverSocket.sync();
	serverSocket.stop();
    }

    @Test
    public void testHeart() {
	ICoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	ISessionFactory sessionFactory = new SessionFactory();
	final ServerSocket serverSocket = ServerSocket.valueOf(SocketChannelConfig.valueOf(8888), 10, 20, coderParserManager, sessionFactory);
	serverSocket.init();
	serverSocket.registerHandle(new SessionHandle(sessionFactory), new HeartHandle(5000));

	Timer timer = new Timer();
	timer.schedule(new TimerTask() {

	    @Override
	    public void run() {
		System.out.println("registerClientSocket");
		ClientSocket clientSocket = serverSocket.registerClient(SocketChannelConfig.valueOf(6969));
		clientSocket.send("连接服务器成功");

		System.out.println("heart time : " + SessionKey.KEY_HEART_TIME.getAttr(clientSocket.getSession()));
		this.cancel();
	    }
	}, 1000);

	serverSocket.start();
	serverSocket.sync();
	serverSocket.stop();
    }

}
