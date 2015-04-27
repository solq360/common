package org.son.chat.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.son.chat.common.net.util.NioUtil;

/**
 * java nio selector 与 Channel 组合 技术
 * @author solq
 */
public class TestNioServer {
	private ServerSocketChannel socketChannel;
	private boolean shutdown = false;
	private Selector selector;

	public TestNioServer() {
		try {
			selector = Selector.open();
			socketChannel = ServerSocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.bind(new InetSocketAddress(6969));
			socketChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void close() {
		if (socketChannel != null) {
			try {
				socketChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {

		int n = 0;
		while (!shutdown) {
			// do select
			try {
				n = selector.select(3000);
				if (shutdown) {
					selector.close();
					break;
				}
			} catch (IOException e) {

			}

			if (n < 0) {
				continue;
			}

			for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
				// 得到下一个Key
				SelectionKey key = i.next();

				// 检查其是否还有效
				if (!key.isValid())
					continue;

 				// 可能存在一次在channel 注册多个操作 不能用 elseif
				try {
					if (key.isAcceptable()) {
						handleAccept(key);
					}
					if (key.isConnectable()) {
						handleConnected(key);
					}
					if (key.isReadable()) {
						handleRead(key);
					}
					if (key.isWritable()) {
						handleWrite(key);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				i.remove();
			}
		}

		selector = null;
		shutdown = false;
	}

	void handleConnected(SelectionKey key) {
		System.out.println(" handleConnected ");
	}

	void handleWrite(SelectionKey key) {
		System.out.println(" handleWrite ");
		NioUtil.clearOps(key, SelectionKey.OP_WRITE);
	}

	void handleAccept(SelectionKey key) {
		System.out.println(" handleAccept ");

		try {
			SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
			clientChannel.configureBlocking(false);
			SelectionKey sk = clientChannel.register(selector, 0);
			NioUtil.setOps(sk, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void handleRead(SelectionKey key) {
		System.out.println(" handleRead ");
		SocketChannel clientChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);

		try {

			buffer.clear();
			long bytesRead = clientChannel.read(buffer);
			if (bytesRead == -1) {

			} else {
  				buffer.flip();
				buffer.clear();
				buffer.put("xxxxx".getBytes());
				buffer.flip();
				clientChannel.write(buffer);
				NioUtil.setOps(key, SelectionKey.OP_WRITE);
 			}

		} catch (Exception e) {
			e.printStackTrace();
			NioUtil.clearOps(key, SelectionKey.OP_READ);
			try {
				clientChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		new TestNioServer().start();
	}
}
