package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.handle.ClientManagerHandle;
import org.son.chat.common.net.core.handle.ISocketHandle;
import org.son.chat.common.net.core.handle.PipeHandle;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.session.ISessionFactory;
import org.son.chat.common.net.core.socket.IServerSocketService;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.IpUtil;
import org.son.chat.common.net.util.NioUtil;

/**
 * 服务端socket 是一个管理多个 客户端 socket 处理 <br>
 * 客户端socket 是一个对一个 socket 处理 <br>
 * 所以我认为 服务端socket其实是维护管理 多个 客户端 socket 这样就大量简化编写 <br>
 * 
 * @author solq
 */
public class ServerSocket extends AbstractISocketChannel implements IServerSocketService {

    public static ServerSocket valueOf(SocketChannelConfig socketChannelConfig, ICoderParserManager coderParserManager, ISessionFactory sessionFactory) {
	ServerSocket serverSocket = new ServerSocket();
	serverSocket.socketChannelConfig = socketChannelConfig;
	serverSocket.coderParserManager = coderParserManager;
	serverSocket.sessionFactory = sessionFactory;
	return serverSocket;
    }

    private ServerSocketChannel socketChannel;
    private ISessionFactory sessionFactory;
    /** 已连接的客户端 */
    private ClientPipeChannel channelClients = new ClientPipeChannel();
    private Thread shutdownHook;

    @Override
    public void init() {
	try {
	    selector = Selector.open();
	    socketChannel = ServerSocketChannel.open();
	    socketChannel.configureBlocking(false);
	    socketChannel.bind(socketChannelConfig.getAddress());
	    socketChannel.register(selector, SelectionKey.OP_ACCEPT);
	    handle = new PipeHandle();
	    ((PipeHandle) handle).register(new ClientManagerHandle(channelClients));
	    this.close = false;
	    this.init = true;
	    registerShutdownHook();
	} catch (IOException e) {
	    throw new NetException("初始化 NIO服务器异常 :", e);
	}
    }

    @Override
    protected void handleAccept(SelectionKey key) {
	System.out.println(" handleAccept ");
	ClientSocket clientSocket = null;
	try {
	    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
	    System.out.println( IpUtil.getAddress(clientChannel.getLocalAddress()) );
	    
	    clientSocket = ClientSocket.valueOfServer(SocketChannelConfig.valueOf(clientChannel.getRemoteAddress()), clientChannel, coderParserManager, handle);
	    clientChannel.configureBlocking(false);
	    final SocketChannelCtx ctx = clientSocket.getCtx();
	    clientSocket.openBefore(ctx);
	    // 必须是新注册的 SelectionKey
	    SelectionKey sk = clientChannel.register(selector, 0, ctx);
	    clientSocket.setSelectionKey(sk);
	    NioUtil.setOps(sk, SelectionKey.OP_READ);
	    clientSocket.openAfter(ctx);
	} catch (IOException e) {
	    if (clientSocket != null) {
		clientSocket.openError(clientSocket.getCtx());
	    }
	    throw new NetException("Socket连接异常 : ", e);
	}
    }

    @Override
    public synchronized void stop() {
	if (selector != null && selector.isOpen()) {
	    try {
		selector.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    selector = null;
	}
	if (socketChannel != null) {
	    try {
		// 业务层通知
		List<ClientSocket> clients = channelClients.getAllClinetSockets();
		for (ClientSocket client : clients) {
		    client.stop();
		}
		socketChannel.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	socketChannel = null;
	super.stop();
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
	    send(client, message, byteBuffer);
	}
    }

    @Override
    public void send(ClientSocket clientSocket, Object message) {
	clientSocket.send(message);
    }

    @Override
    public void send(ClientSocket clientSocket, Object message, ByteBuffer byteBuffer) {
	clientSocket.send(message, byteBuffer);
    }

    @Override
    public ClientSocket registerClientSocket(SocketChannelConfig config) {
	System.out.println(" handleConnect ");
	try {
	    ClientSocket clientSocket = ClientSocket.valueOf(config, this.coderParserManager, this.handle);
	    clientSocket.openServerMode(this.selector);
	    clientSocket.init();
	    return clientSocket;
	} catch (Exception e) {
	    throw new NetException("Socket连接异常 : ", e);
	}
    }

    @Override
    public void registerHandle(ISocketHandle... handleArray) {
	for(ISocketHandle handle : handleArray){
	    ((PipeHandle) this.handle).register(handle);
	}
    }

    @Override
    public ISession createSession() {
	return sessionFactory.createSession();
    }

    private void registerShutdownHook() {
	if (this.shutdownHook == null) {
	    this.shutdownHook = new Thread() {
		@Override
		public void run() {
		    ServerSocket.this.stop();
		}
	    };
	    Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}
    }

}
