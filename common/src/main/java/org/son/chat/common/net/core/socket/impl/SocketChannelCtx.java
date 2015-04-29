package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.IcoderCtx;
import org.son.chat.common.net.exception.NetException;

/**
 * 主要负责解码，会话数据
 * @author solq
 */
public class SocketChannelCtx implements IcoderCtx {

	public static SocketChannelCtx valueOf(Selector selector, SocketChannel channel,ICoderParserManager coderParserManager) {
		SocketChannelCtx result = new SocketChannelCtx();
		result.selector = selector;
		result.channel = channel;
		result.coderParserManager = coderParserManager;
		return result;
	}

	/******** jdk nio 选择器 ************/
	private Selector selector;
	/******** jdk nio socket channel ************/
	private SocketChannel channel;
	/******** jdk nio byteBuffer ************/
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024 * 4);

	/** 编/解器管理器 */
	private ICoderParserManager coderParserManager;

	private int writeIndex;

	public void send(Object message) {
		ByteBuffer sendMessage = coderParserManager.encode(message, this);
		sendMessage.flip();
		try {
			this.channel.write(sendMessage);
		} catch (IOException e) {
			throw new NetException("发送消息出错 :", e);
		}
	}

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

}
