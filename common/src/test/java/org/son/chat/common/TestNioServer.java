package org.son.chat.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.son.chat.common.net.ClientSocket;
import org.son.chat.common.net.handle.ISocketHandle;
import org.son.chat.common.net.util.NioUtil;

/**
 * java nio selector 与 Channel 组合 技术
 * @author solq
 */
public class TestNioServer extends ClientSocket {
	private ServerSocketChannel socketChannel;

	@Override
	public void init() {
		try {
			selector = Selector.open();
			socketChannel = ServerSocketChannel.open();
			socketChannel.configureBlocking(false);
			socketChannel.bind(new InetSocketAddress(6969));
			socketChannel.register(selector, SelectionKey.OP_ACCEPT);
			this.close = false;
			this.socketHandle = new ISocketHandle() {

				@Override
				public void unRegister(AbstractSelectableChannel channel) {

				}

				@Override
				public void register(AbstractSelectableChannel channel) {

				}

				@Override
				public void open(AbstractSelectableChannel channel) {

				}

				@Override
				public void close(AbstractSelectableChannel channel) {

				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void handleRead(final SelectionKey key) {
		System.out.println(" handleRead ");
		final SocketChannel clientChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);

		try {
			buffer.clear();
			long bytesRead = clientChannel.read(buffer);
			if (bytesRead == -1) {

			} else {
				Timer timer=new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						for (int i = 0; i < 10; i++) {
							try {
								testWrite(clientChannel, i);
							} catch (IOException e) {
 								e.printStackTrace();
							}
						}
						NioUtil.setOps(key, SelectionKey.OP_WRITE);		
						System.out.println(" push message");
					}
				}, new Date(),5000L);

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

	private void testWrite(SocketChannel channel, int i) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
		buffer.put(("a" + i).getBytes());
		buffer.flip();
		channel.write(buffer);
	}

	public static void main(String[] args) {
		new TestNioServer().start();
	}
}
