package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderCtx;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.handle.ISocketHandle;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.socket.IChannel;
import org.son.chat.common.net.core.socket.IClientSocketService;
import org.son.chat.common.net.core.socket.ISocketPool;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;

/**
 * @author solq
 */
public class ClientSocket extends AbstractISocketChannel implements IClientSocketService, IChannel {

    public static ClientSocket valueOf(SocketChannelConfig socketChannelConfig, ISocketPool pool, ICoderParserManager coderParserManager, ISocketHandle socketHandle) {
	ClientSocket clientSocket = new ClientSocket();
	clientSocket.pool = pool;
	clientSocket.socketChannelConfig = socketChannelConfig;
	clientSocket.coderParserManager = coderParserManager;
	clientSocket.handle = socketHandle;
	clientSocket.ctx = SocketChannelCtx.valueOf(clientSocket);
	return clientSocket;
    }

    public static ClientSocket valueOfServer(SocketChannelConfig socketChannelConfig, SocketChannel channel, ISocketPool pool, ICoderParserManager coderParserManager, ISocketHandle socketHandle) {
	ClientSocket clientSocket = new ClientSocket();
	clientSocket.pool = pool;
	clientSocket.handle = socketHandle;
	clientSocket.channel = channel;
	clientSocket.coderParserManager = coderParserManager;
	clientSocket.socketChannelConfig = socketChannelConfig;
	clientSocket.connected = true;
	clientSocket.close = false;
	clientSocket.serverMode = true;
	clientSocket.ctx = SocketChannelCtx.valueOf(clientSocket);
	return clientSocket;
    }

    private SocketAddress localAddress;
    private SocketAddress remoteAddress;
    private SocketChannel channel;

    private SocketChannelCtx ctx;
    private SelectionKey selectionKey;
    private ISession session;

    private boolean connected = false;
    private String nameChannel;
    private boolean serverMode = false;

    @Override
    public void send(Object message) {
	ByteBuffer byteBuffer = coderParserManager.encode(message, ctx);
	byteBuffer.flip();
	send(message, byteBuffer);
    }

    /**
     * nio channel 发送真恶心.... <br>
     * 发送数据参考 http://ericbaner.iteye.com/blog/1821798
     */
    @Override
    public void send(final Object message, final ByteBuffer byteBuffer) {
	getPool().execute(new Runnable() {

	    @Override
	    public void run() {
		try {
		    if (ClientSocket.this.close) {
			return;
		    }
		    ClientSocket.this.writeBefore(ctx, message);
		    while (byteBuffer.hasRemaining()) {
			int len = ClientSocket.this.channel.write(byteBuffer);

			if (len < 0) {
			    throw new NetException("发送消息出错 :" + len);
			}

			// 写半包处理
			if (len == 0) {
			    System.out.println("写半包");
			    NioUtil.setOps(selectionKey, SelectionKey.OP_WRITE);
			    // selector.wakeup();
			    break;
			}
			ClientSocket.this.writeAfter(ctx, message);
		    }
		} catch (IOException e) {
		    ClientSocket.this.writeError(ClientSocket.this.ctx, message);
		    coderParserManager.error(byteBuffer, ClientSocket.this.ctx);
		    throw new NetException("发送消息出错 :", e);
		}
	    }
	});
    }

    @Override
    public void init() {
	try {
	    this.init = true;
	    this.openBefore(ctx);
	    channel = SocketChannel.open(this.socketChannelConfig.getAddress());
	    channel.configureBlocking(false);
	    this.connected = channel.isConnected();
	    if (!this.connected) {
		// this.channel.register(this.selector,
		// SelectionKey.OP_CONNECT);
		// TODO 定时检查连接
	    } else {
		finishConnect();
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	    this.openError(ctx);
	    stop();
	}
    }

    private void finishConnect() throws IOException {

	while (!channel.finishConnect()) {
	    // TODO 超时处理
	}
	
	selectionKey = channel.register(getSelector(),0, this);
	NioUtil.clearOps(this.selectionKey, SelectionKey.OP_ACCEPT);
	NioUtil.setOps(this.selectionKey, SelectionKey.OP_READ);
	this.close = false;
	this.openAfter(this.ctx);
	// NioUtil.setOps(selectionKey, SelectionKey.OP_WRITE);
	this.pool.init();
    }

    @Override
    public synchronized void stop() {
	if (channel != null) {
	    try {
		// 业务层通知
		this.closeBefore(ctx);
		if (channel.isConnected()) {
		    channel.close();
		}
		this.closeAfter(ctx);

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	if (!serverMode) {
	    try {
		getPool().shutdown();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	ctx = null;
	channel = null;
	selectionKey = null;
	session = null;
	super.stop();
    }

    @Override
    public void start() {
	if (serverMode) {
	    return;
	}
	super.start();
    }

    @Override
    public void openBefore(ICoderCtx ctx) {
	handle.openBefore(ctx);
    }

    @Override
    public void openAfter(ICoderCtx ctx) {
	handle.openAfter(ctx);
    }

    @Override
    public void closeBefore(ICoderCtx ctx) {
	handle.closeBefore(ctx);
    }

    @Override
    public void closeAfter(ICoderCtx ctx) {
	handle.closeAfter(ctx);
    }

    @Override
    public void readBefore(ICoderCtx ctx, Object request) {
	handle.readBefore(ctx, request);
    }

    @Override
    public void readAfter(ICoderCtx ctx, Object request) {
	handle.readAfter(ctx, request);
    }

    @Override
    public void writeBefore(ICoderCtx ctx, Object response) {
	handle.writeBefore(ctx, response);
    }

    @Override
    public void writeAfter(ICoderCtx ctx, Object response) {
	handle.writeAfter(ctx, response);
    }

    @Override
    public void openError(ICoderCtx ctx) {
	handle.openError(ctx);

    }

    @Override
    public void writeError(ICoderCtx ctx, Object response) {
	handle.writeError(ctx, response);
    }

    // getter

    public SelectionKey getSelectionKey() {
	return selectionKey;
    }

    public void setSelectionKey(SelectionKey selectionKey) {
	this.selectionKey = selectionKey;
    }

    public boolean isConnected() {
	return connected;
    }

    @Override
    public boolean isClose() {
	return this.close;
    }

    @Override
    public boolean isConected() {
	return this.connected;
    }

    public SocketChannelCtx getCtx() {
	return ctx;
    }

    @Override
    public void buildAddress() {
	this.localAddress = getLocalAddress();
	this.remoteAddress = getRemoteAddress();
    }

    @Override
    public SocketAddress getLocalAddress() {
	if (localAddress != null) {
	    return localAddress;
	}
	try {
	    return channel.getLocalAddress();
	} catch (IOException e) {
	    return null;
	}
    }

    @Override
    public SocketAddress getRemoteAddress() {
	if (remoteAddress != null) {
	    return remoteAddress;
	}
	try {
	    return channel.getRemoteAddress();
	} catch (IOException e) {
	    return null;
	}
    }

    @Override
    public void setChannelName(String nameChannel) {
	this.nameChannel = nameChannel;
    }

    @Override
    public String getChannelName() {
	return nameChannel;
    }

    @Override
    public void setSession(ISession session) {
	this.session = session;
    }

    @Override
    public ISession getSession() {
	return session;
    }

    public void openServerMode() {
	this.serverMode = true;
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
    }
}
