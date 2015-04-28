package org.son.chat.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.AbstractISocketChannel;
import org.son.chat.common.net.core.IClientSocketService;
import org.son.chat.common.net.core.SocketChannelCtx;
import org.son.chat.common.net.util.NioUtil;

/**
 * @author solq
 */
public class ClientSocket extends AbstractISocketChannel implements IClientSocketService {

	public static ClientSocket valueOf(SocketChannelConfig socketChannelConfig) {
		ClientSocket clientSocket = new ClientSocket();
		clientSocket.socketChannelConfig = socketChannelConfig;
		return clientSocket;
	}

	// private Date heartbeatTime = new Date();
	private boolean connected = false;
	private SocketChannel channel;

	@Override
	public void start() {
		init();
		while (!close) {
			try {
				int n = selector.select(10);
				if (n < 0) {
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
				// 得到下一个Key
				SelectionKey key = i.next();
				try {
					handle(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				i.remove();
			}
		}
		stop();
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

		if (channel != null && channel.isConnected()) {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public byte[] read() {
		return null;
	}

	@Override
	public void send(byte[] msg) {
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
		SocketChannelCtx ctx = SocketChannelCtx.valueOf(selector, channel);
		SelectionKey sk = channel.register(this.selector, 0, ctx);
		NioUtil.setOps(sk, SelectionKey.OP_READ);
		this.close = false;
		
		writeConnect(channel, 2);
		NioUtil.setOps(sk, SelectionKey.OP_WRITE);
	}

	private void writeConnect(SocketChannel channel, int i) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
		buffer.put(("a" + i).getBytes());
		buffer.flip();
		channel.write(buffer);
	}
}
