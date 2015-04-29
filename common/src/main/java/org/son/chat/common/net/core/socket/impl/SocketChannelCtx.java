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

	public static SocketChannelCtx valueOf(Selector selector, SocketChannel channel, ICoderParserManager coderParserManager) {
		SocketChannelCtx result = new SocketChannelCtx();
		result.selector = selector;
		result.channel = channel;
		result.coderParserManager = coderParserManager;
		result.readBuffer = result.createByteBuffer(result.maxReadBufferSize);
		return result;
	}

	/******** jdk nio 选择器 ************/
	private Selector selector;
	/******** jdk nio socket channel ************/
	private SocketChannel channel;
	/******** jdk nio byteBuffer ************/

	private int maxReadBufferSize = 1024 * 20;
	private int minReadBufferSize = 1024 * 8;

	private ByteBuffer readBuffer;

	/** 编/解器管理器 */
	private ICoderParserManager coderParserManager;

	private int writeIndex = 0;

	private int currPackageIndex = 0;

	public void send(Object message) {
		ByteBuffer sendMessage = coderParserManager.encode(message, this);
		sendMessage.flip();
		try {
			this.channel.write(sendMessage);
		} catch (IOException e) {
			throw new NetException("发送消息出错 :", e);
		}
	}

	public synchronized void addWriteIndex(long len) {
		if (len > 0) {
			writeIndex += len;
		}
	}

	/**
	 * 标记下一次读数据
	 */
	public synchronized void nextPackageIndex(long len) {
		if (len > 0) {
			currPackageIndex += len;
		}
	}

	/**
	 * 准备开始读数据处理
	 */
	public synchronized ByteBuffer readBegin() {
		readBuffer.clear();
		readBuffer.position(writeIndex);

		final int unUseSize = readBuffer.capacity() - writeIndex;

		System.out.println("read writeIndex  :" + writeIndex);

		// 扩容处理
		if (minReadBufferSize > unUseSize) {
			// 计算容量
			final int bufferSize = this.maxReadBufferSize;
			ByteBuffer newReadBuffer = createByteBuffer(bufferSize);
			newReadBuffer.put(readBuffer.array(), writeIndex, readBuffer.limit() - writeIndex);
			writeIndex = 0;
			currPackageIndex = 0;
			readBuffer = newReadBuffer;
			System.out.println("扩容处理 ");
		}
		return readBuffer;
	}

	private ByteBuffer createByteBuffer(int maxReadBufferSize) {
		return ByteBuffer.allocate(maxReadBufferSize);
	}

	// getter

	public int getWriteIndex() {
		return writeIndex;
	}

	public int getMaxReadBufferSize() {
		return maxReadBufferSize;
	}

	public int getMinReadBufferSize() {
		return minReadBufferSize;
	}

	public int getCurrPackageIndex() {
		return currPackageIndex;
	}

	public Selector getSelector() {
		return selector;
	}

}
