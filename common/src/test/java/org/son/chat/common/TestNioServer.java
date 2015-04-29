package org.son.chat.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.socket.impl.ClientSocket;
import org.son.chat.common.protocol.PackageDefaultCoder;

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

			coderParserManager = new CoderParserManager();
			coderParserManager.register(CoderParser.valueOf("server chat", PackageDefaultCoder.valueOf(), new ChatTestServerHandle()));

			this.close = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestNioServer().start();
	}
}
