package org.son.chat.common.net.core;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 主要负责解码，会话数据
 * @author solq
 */
public class SocketChannelCtx {

	public static SocketChannelCtx valueOf(Selector selector, SocketChannel channel) {
		SocketChannelCtx result = new SocketChannelCtx();
		result.selector = selector;
		result.channel = channel;
		return result;
	}

	/******** jdk nio 选择器 ************/
	private Selector selector;
	/******** jdk nio socket channel ************/
	private SocketChannel channel;
	/******** jdk nio byteBuffer ************/
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 4);

	
	private int writeIndex;

	public void changeWriteIndex(long len) {
		if (len > 0) {
			writeIndex += len;
		}
	}

	public int getLength() {
		return this.readBuffer.capacity() - writeIndex;
	}

	// getter
	public ByteBuffer getReadBuffer() {
		return readBuffer;
	}

	public int getWriteIndex() {
		return writeIndex;
	}

	public Selector getSelector() {
		return selector;
	}

	public SocketChannel getChannel() {
		return channel;
	}
}
