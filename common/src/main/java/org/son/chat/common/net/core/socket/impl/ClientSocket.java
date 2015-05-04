package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.socket.IClientSocketService;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;
import org.son.chat.common.net.util.SocketPoolFactory;
import org.son.chat.common.protocol.ChatHandle;
import org.son.chat.common.protocol.PackageDefaultCoder;

/**
 * @author solq
 */
public class ClientSocket extends AbstractISocketChannel implements IClientSocketService {

	public static ClientSocket valueOf(SocketChannelConfig socketChannelConfig) {
		ClientSocket clientSocket = new ClientSocket();
		clientSocket.socketChannelConfig = socketChannelConfig;
		CoderParserManager coderParserManager = new CoderParserManager();
		clientSocket.coderParserManager = coderParserManager;
		coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
		SocketChannelCtx ctx = SocketChannelCtx.valueOf(clientSocket);
		clientSocket.ctx = ctx;
		return clientSocket;
	}

	public static ClientSocket valueOfServer(SocketChannelConfig socketChannelConfig, SocketChannel channel, ICoderParserManager coderParserManager) {
		ClientSocket clientSocket = new ClientSocket();
		clientSocket.channel = channel;
		clientSocket.coderParserManager = coderParserManager;
		clientSocket.socketChannelConfig = socketChannelConfig;
		clientSocket.connected = true;
		clientSocket.close = false;

		SocketChannelCtx ctx = SocketChannelCtx.valueOf(clientSocket);
		clientSocket.ctx = ctx;
		return clientSocket;
	}

	// private Date heartbeatTime = new Date();
	private boolean connected = false;
	private SocketChannel channel;

	private SelectionKey selectionKey;
	private SocketChannelCtx ctx;

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

		if (channel != null && channel.isConnected()) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void send(Object message) {
		ByteBuffer byteBuffer = coderParserManager.encode(message, ctx);
		byteBuffer.flip();
		send(byteBuffer);
	}

	/**
	 * nio channel 发送真恶心.... <br>
	 * 发送数据参考 http://ericbaner.iteye.com/blog/1821798
	 */
	@Override
	public void send(final ByteBuffer byteBuffer) {
		SocketPoolFactory.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				try {
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
					}
				} catch (IOException e) {
					coderParserManager.error(byteBuffer, null);
					throw new NetException("发送消息出错 :", e);
				}
			}
		});
	}

	@Override
	public void init() {
		try {
			channel = SocketChannel.open(this.socketChannelConfig.getRemoteAddress());
			channel.configureBlocking(false);
			this.selector = Selector.open();
			this.connected = channel.isConnected();
			if (!this.connected) {
				// this.channel.register(this.selector, SelectionKey.OP_CONNECT);
				// TODO 定时检查连接
			} else {
				finishConnect();
			}

		} catch (IOException e) {
			e.printStackTrace();
			stop();
		}
	}

	private void finishConnect() throws IOException {

		while (!channel.finishConnect()) {
			// TODO 超时处理
		}
		selectionKey = channel.register(this.selector, 0, ctx);
		NioUtil.setOps(selectionKey, SelectionKey.OP_READ);
		this.close = false;
		this.ctx.send("连接服务器成功");
		NioUtil.setOps(selectionKey, SelectionKey.OP_WRITE);
	}

	public SocketChannelCtx getCtx() {
		return ctx;
	}

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public void setSelectionKey(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

}
