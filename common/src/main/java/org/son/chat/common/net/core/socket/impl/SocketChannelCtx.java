package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.IcoderCtx;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;

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
	private ByteBuffer readBuffer;
	/******** jdk nio channel selectionKey ************/
	private SelectionKey selectionKey;
	private int maxReadBufferSize = 1024 * 20;
	private int minReadBufferSize = 1024 * 8;

	/** 编/解器管理器 */
	private ICoderParserManager coderParserManager;

	private int writeIndex = 0;

	private int currPackageIndex = 0;

	/**
	 * nio channel 发送真恶心.... <br>
	 * 发送数据参考 http://ericbaner.iteye.com/blog/1821798
	 */
	public void send(Object message) {
		ByteBuffer sendMessage = coderParserManager.encode(message, this);
		sendMessage.flip();

		try {
			while (sendMessage.hasRemaining()) {
				int len = this.channel.write(sendMessage);
				if (len < 0) {
					throw new NetException("发送消息出错 :" + len);
				}

				if (len == 0) {
					System.out.println("send end");
					NioUtil.setOps(selectionKey, SelectionKey.OP_WRITE);
					// selector.wakeup();
					break;
				}
			}
		} catch (IOException e) {
			coderParserManager.error(sendMessage, this);
			throw new NetException("发送消息出错 :", e);
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
	

	/**
	 * 更改NIO 每次读索引
	 */
	public synchronized void readEnd(long len) {
		if (len > 0) {
			writeIndex += len;
			readBuffer.limit(writeIndex);
		}
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

	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	public void setSelectionKey(SelectionKey selectionKey) {
		this.selectionKey = selectionKey;
	}

}
