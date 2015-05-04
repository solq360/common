package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.List;

import org.son.chat.common.ChatTestServerHandle;
import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.socket.IServerSocketService;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.protocol.PackageDefaultCoder;

/**
 * 服务端socket 是一个管理多个 客户端 socket 处理 <br>
 * 客户端socket 是一个对一个 socket 处理 <br>
 * 所以我认为 服务端socket其实是维护管理 多个 客户端 socket 这样就大量简化编写 <br>
 * @author solq
 */
public class ServerSocket extends AbstractISocketChannel implements IServerSocketService {

	public static ServerSocket valueOf(SocketChannelConfig socketChannelConfig) {
		ServerSocket serverSocket = new ServerSocket();
		serverSocket.socketChannelConfig = socketChannelConfig;
		CoderParserManager coderParserManager = new CoderParserManager();
		serverSocket.coderParserManager = coderParserManager;
		coderParserManager.register(CoderParser.valueOf("server chat", PackageDefaultCoder.valueOf(), new ChatTestServerHandle()));
		return serverSocket;
	}

	private ServerSocketChannel socketChannel;
	/** 已连接的客户端 */
	private ClientPipeChannel channelClients = new ClientPipeChannel();

	@Override
	public void init() {
		try {
			selector = Selector.open();
			socketChannel = ServerSocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.bind(socketChannelConfig.getLocalAddress());
			socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			this.close = false;
		} catch (IOException e) {
			throw new NetException("初始化 NIO服务器异常 :", e);
		}
	}

	@Override
	public void stop() {
		if (selector != null && selector.isOpen()) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			selector = null;
			// 业务层通知
			close();
		}
		close = false;

		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void sendAll(Object message) {
		ByteBuffer byteBuffer = coderParserManager.encode(message, null);
		List<ClientSocket> clients = channelClients.getAllClinetSockets();
		for (ClientSocket client : clients) {
			send(client, byteBuffer);
		}
	}

	@Override
	public void send(String channelName, Object message) {
		ByteBuffer byteBuffer = coderParserManager.encode(message, null);
		List<ClientSocket> clients = channelClients.getChannelClinetSockets(channelName);
		for (ClientSocket client : clients) {
			send(client, byteBuffer);
		}
	}

	@Override
	public void send(ClientSocket clientSocket, Object message) {
		clientSocket.send(message);
	}

	@Override
	public void send(ClientSocket clientSocket, ByteBuffer byteBuffer) {
		clientSocket.send(byteBuffer);
	}

}
