package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.socket.IClientSocketService;
import org.son.chat.common.net.util.NioUtil;
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
		return clientSocket;
	}

	// private Date heartbeatTime = new Date();
	private boolean connected = false;
	private SocketChannel channel;

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
		// try {
		// this.channel.write(ByteBuffer.wrap(msg));
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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
		SocketChannelCtx ctx = SocketChannelCtx.valueOf(selector, channel, this.coderParserManager);
		SelectionKey sk = channel.register(this.selector, 0, ctx);
		ctx.setSelectionKey(sk);
		NioUtil.setOps(sk, SelectionKey.OP_READ);
		this.close = false;

		ctx.send("连接服务器成功");
		NioUtil.setOps(sk, SelectionKey.OP_WRITE);
	}

}
