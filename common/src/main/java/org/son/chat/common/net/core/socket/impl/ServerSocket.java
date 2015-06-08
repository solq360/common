package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
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
import org.son.chat.common.net.core.socket.ISocketPool;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;

/**
 * 服务端socket 是一个管理多个 客户端 socket 处理 <br>
 * 客户端socket 是一个对一个 socket 处理 <br>
 * 所以我认为 服务端socket其实是维护管理 多个 客户端 socket 这样就大量简化编写 <br>
 * 
 * @author solq
 */
public class ServerSocket extends AbstractISocketChannel implements IServerSocketService {

    public static ServerSocket valueOf(SocketChannelConfig socketChannelConfig, int minPoolSize, int maxPoolSize, ICoderParserManager coderParserManager, ISessionFactory sessionFactory) {
	ServerSocket serverSocket = new ServerSocket();
	serverSocket.minPoolSize = minPoolSize;
	serverSocket.maxPoolSize = maxPoolSize;
	serverSocket.socketChannelConfig = socketChannelConfig;
	serverSocket.coderParserManager = coderParserManager;
	serverSocket.sessionFactory = sessionFactory;
	return serverSocket;
    }

    private ServerSocketChannel socketChannel;
    private ISessionFactory sessionFactory;
    private ClientPipeChannel channelClients = new ClientPipeChannel();
    private Thread shutdownHook;

    private int count;
    private int minPoolSize;
    private int maxPoolSize;
    private ISocketPool[] groupPool;

    @Override
    public void init() {
	try {
	    groupPool = new SocketPool[minPoolSize];
	    for (int i = 0; i < minPoolSize; i++) {
		groupPool[i] = new SocketPool("server : " + i, null);
	    }
	    pool = new SocketPool("server accept", this);	  
	    socketChannel = ServerSocketChannel.open();
	    socketChannel.configureBlocking(false);
	    socketChannel.bind(socketChannelConfig.getAddress());
	    socketChannel.register(getSelector(), SelectionKey.OP_ACCEPT);
	    handle = new PipeHandle();
	    ((PipeHandle) handle).register(new ClientManagerHandle(channelClients));
	    this.close = false;
	    this.init = true;
	    pool.init();
	    registerShutdownHook();
	} catch (IOException e) {
	    throw new NetException("初始化 NIO服务器异常 :", e);
	}
    }

    @Override
    public void doAccept(SelectionKey key) {
	System.out.println(" handleAccept ");
	ClientSocket clientSocket = null;
	try {
	    SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
	    clientChannel.configureBlocking(false);
	    ISocketPool pool = getNextPool();
	    clientSocket = ClientSocket.valueOfServer(SocketChannelConfig.valueOf(clientChannel.getRemoteAddress()), clientChannel,pool, coderParserManager, handle);

	    final SocketChannelCtx ctx = clientSocket.getCtx();
	    clientSocket.openBefore(ctx);
	    // 必须是新注册的 SelectionKey
	    SelectionKey sk = clientChannel.register(pool.getSelector(), 0, clientSocket);
	    clientSocket.setSelectionKey(sk);
	    NioUtil.setOps(sk, SelectionKey.OP_READ);
	    clientSocket.openAfter(ctx);
	    pool.init();

	} catch (IOException e) {
	    if (clientSocket != null) {
		clientSocket.openError(clientSocket.getCtx());
	    }
	    throw new NetException("Socket连接异常 : ", e);
	}
    }

    @Override
    public synchronized void stop() {
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
	pool.shutdown();
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
    public ClientSocket registerClient(SocketChannelConfig config) {
	System.out.println(" registerClient ");
	try {
	    ClientSocket clientSocket = ClientSocket.valueOf(config, getNextPool(), this.coderParserManager, this.handle);
	    clientSocket.openServerMode();
	    clientSocket.init();
	    return clientSocket;
	} catch (Exception e) {
	    throw new NetException("Socket连接异常 : ", e);
	}
    }

    @Override
    public void registerHandle(ISocketHandle... handleArray) {
	for (ISocketHandle handle : handleArray) {
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

    @Override
    public ISocketPool getNextPool() {
	ISocketPool pool = getGroupPool()[count++ % getGroupPool().length];
 	return pool;
    }

    @Override
    public ISocketPool[] getGroupPool() {
	return groupPool;
    }

    public int getMinPoolSize() {
	return minPoolSize;
    }

    public int getMaxPoolSize() {
	return maxPoolSize;
    }

    @Override
    public void sync() {
	while (pool.isRun()) {
	    try {
		Thread.sleep(5000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	while (true) {
	    boolean sleep = false;
	    for (ISocketPool sp : groupPool) {
		if (sp.isRun()) {
		    sleep = true;
		    break;
		}
	    }
	    if(!sleep){
		break;
	    }
	    try {
		Thread.sleep(5000);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

    }

}
