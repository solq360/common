package org.son.chat.common.net.core.socket.impl;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.coder.IcoderCtx;

/**
 * 主要负责解码，会话数据
 * @author solq
 */
public class SocketChannelCtx implements IcoderCtx {

	public static SocketChannelCtx valueOf(ClientSocket clientSocket) {
		SocketChannelCtx result = new SocketChannelCtx();
		result.clientSocket = clientSocket;
		result.readBuffer = result.createByteBuffer(result.maxReadBufferSize);
 		return result;
	}

	/******** jdk nio byteBuffer ************/
	private ByteBuffer readBuffer;

	private ClientSocket clientSocket;
	private int maxReadBufferSize = 1024 * 20;
	private int minReadBufferSize = 1024 * 8;

	private final static int MIN_MUT = 1422;
	private final static int DOUBLE_MUT = MIN_MUT * 2;

	private int writeIndex = 0;

	private int currPackageIndex = 0;

	public void send(Object message) {
		clientSocket.send(message);
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
		boolean isExt = false;
		int bufferSize = maxReadBufferSize;

		// 剩余容量低于最少边界
		if (DOUBLE_MUT > unUseSize) {
			// 2倍方式扩容
			final int usePackageSize = writeIndex - this.currPackageIndex;
			final int doubleUsePackageSize = usePackageSize * 2;
			if (doubleUsePackageSize > readBuffer.capacity()) {
				bufferSize = readBuffer.capacity() << 1;
			} else {
				bufferSize = Math.max(doubleUsePackageSize, maxReadBufferSize);
			}
			isExt = true;

		} else if (unUseSize > maxReadBufferSize) { // 剩余容量高于最大边界 缩容处理
			final int usePackageSize = writeIndex - this.currPackageIndex;
			final int doubleUsePackageSize = usePackageSize * 2;
			if (maxReadBufferSize > doubleUsePackageSize) {
				bufferSize = maxReadBufferSize;
				isExt = true;
			}
		}

		if (isExt) {
			extendByteBuffer(bufferSize);
		}

		return readBuffer;
	}

	private void extendByteBuffer(int bufferSize) {
		ByteBuffer newReadBuffer = createByteBuffer(bufferSize);
		// 从最后包标记开始复制
		newReadBuffer.put(readBuffer.array(), this.currPackageIndex, this.writeIndex - this.currPackageIndex);
		writeIndex = 0;
		currPackageIndex = 0;
		readBuffer = newReadBuffer;
	}

	public synchronized ByteBuffer coderBegin() {
		readBuffer.clear().position(currPackageIndex).limit(writeIndex);
		return readBuffer.asReadOnlyBuffer();
	}

	// 模拟socket read
	public synchronized int readBuffer(ByteBuffer buffer) {
		buffer.flip();
		readBuffer.clear();
		readBuffer.position(writeIndex);
		readBuffer.put(buffer);
		return buffer.limit();
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

	public ClientSocket getClientSocket() {
		return clientSocket;
	}

}
