package org.son.chat.common.net.core;

import java.nio.ByteBuffer;

/**
 * 主要负责解码，会话数据
 * @author solq
 */
public class SocketChannelCtx {
	public static SocketChannelCtx valueOf() {
		SocketChannelCtx result = new SocketChannelCtx();
		return result;
	}

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
}
