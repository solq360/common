package org.son.chat.common.net.core;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.handle.ISocketHandle;
import org.son.chat.common.net.util.NioUtil;

/**
 * socketchannel 模板
 * @author solq
 */
public abstract class AbstractISocketChannel implements ISocketChannel {

	/******** jdk nio 选择器 ************/
	protected Selector selector;
	/******** jdk nio socket channel ************/
	protected SocketChannel channel;

	protected boolean close = true;

	protected ICoderParser coderParser;
	protected ISocketHandle socketHandle;
	protected SocketChannelConfig socketChannelConfig;

	@Override
	public SocketChannelConfig getSocketChannelConfig() {
		return socketChannelConfig;
	}

	@Override
	public void open() {
		socketHandle.open(channel);
	}

	@Override
	public void register() {
		socketHandle.register(channel);
	}

	@Override
	public void close() {
		socketHandle.close(channel);
	}

	@Override
	public void unRegister() {
		socketHandle.unRegister(channel);
	}

	protected void handleAccept(SelectionKey key) {
		System.out.println(" handleAccept ");
		try {
			SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
			clientChannel.configureBlocking(false);
			clientChannel.register(selector, 0);
			NioUtil.setOps(key, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void handleConnected(SelectionKey key) {
		System.out.println(" handleConnected ");
		open();
		NioUtil.setOps(key, SelectionKey.OP_READ);
	}

	protected void handleWrite(SelectionKey key) {
		System.out.println(" handleWrite ");
		// 服务端读到东西后，注册写事件。等写完东西后取消写事件的注册。
		NioUtil.clearOps(key, SelectionKey.OP_WRITE);
	}

	protected void handleRead(SelectionKey key) {
		System.out.println(" handleRead ");

		SocketChannel clientChannel = (SocketChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(1024 * 4);
		buffer.clear();

		try {
			long bytesRead = clientChannel.read(buffer);
			if (bytesRead == -1) {

			} else {
				buffer.flip();
				System.out.println(" read data : " + new String(buffer.array(), 0, buffer.limit()));

				// 服务端读到东西后，注册写事件。等写完东西后取消写事件的注册。
				NioUtil.setOps(key, SelectionKey.OP_WRITE);
			}

		} catch (Exception e) {
			NioUtil.clearOps(key, SelectionKey.OP_READ);
			e.printStackTrace();
			try {
				clientChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}
}
