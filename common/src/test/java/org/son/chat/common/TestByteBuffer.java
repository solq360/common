package org.son.chat.common;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;
import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderResult;
import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;
import org.son.chat.common.net.exception.CoderException;
import org.son.chat.common.net.util.ByteHelper;
import org.son.chat.common.protocol.ChatHandle;
import org.son.chat.common.protocol.PackageDefaultCoder;

/**
 * @author solq
 */
public class TestByteBuffer {

    @Test
    public void testByteBuffer() {
	ByteBuffer bb = ByteBuffer.allocate(50);
	bb.put((byte) 1);
	bb.put((byte) 2);
	bb.put((byte) 3);
	bb.put((byte) 4);
	bb.put((byte) 5);
	Assert.assertEquals(3, bb.get(2));
    }

    @Test
    public void testAutoExtend() {
	ByteBuffer bb = ByteBuffer.allocate(1);
	bb.put((byte) 1);
	bb.put((byte) 2);
	bb.put((byte) 3);
	bb.put((byte) 4);
	bb.put((byte) 5);
    }

    @Test
    public void testSharedBuffer() {
	ByteBuffer bb = ByteBuffer.allocate(50);
	bb.put((byte) 1);
	bb.put((byte) 2);
	bb.put((byte) 3);

	ByteBuffer cloneBB = bb.asReadOnlyBuffer();
	bb.put((byte) 4);

	Assert.assertEquals(4, cloneBB.get(3));
	Assert.assertEquals(4, cloneBB.get(3));

	cloneBB.position(3);
	bb.position(5);
	Assert.assertEquals(4, cloneBB.get());

    }

    @Test
    public void testByteContains() {
	byte[] b1 = { 48, 1, 0, 0 };
	byte[] b2 = { 0, 1 };
	boolean flag = ByteHelper.contains(b2, b1);
	Assert.assertEquals(false, flag);
    }

    @Test
    public void testByteBufferPutInt() {
	String body = "发送数据 ： 11";
	int bodyLength = body.getBytes().length;
	ByteBuffer bb = ByteBuffer.allocate(2 + 4 + bodyLength);
	bb.put((byte) 1);
	bb.put((byte) 2);
	bb.putInt(bodyLength);
	bb.put(body.getBytes());

	bb.flip();

	Assert.assertEquals(bodyLength, bb.getInt(2));

    }

    @Test
    public void testSend() throws InterruptedException {
	CoderParserManager coderParserManager = new CoderParserManager();
	coderParserManager.register(CoderParser.valueOf("chat", PackageDefaultCoder.valueOf(), new ChatHandle()));
	SocketChannelCtx socketChannelCtx = SocketChannelCtx.valueOf(null);

	int count = 0;
	while (true) {
	    count++;
	    ByteBuffer sendBuffer = coderParserManager.encode("发送数据 : " + count, socketChannelCtx);
	    ByteBuffer buffer = socketChannelCtx.readBegin();
	    // 模拟socket read
	    int writeSize = socketChannelCtx.readBuffer(sendBuffer);
	    socketChannelCtx.readEnd(writeSize);
	    boolean run = true;
	    // 粘包处理
	    while (run) {
		buffer = socketChannelCtx.coderBegin();
		buffer.mark();
		CoderResult coderResult = coderParserManager.decode(buffer, socketChannelCtx);
		switch (coderResult.getValue()) {
		case SUCCEED:
		    break;
		case NOT_FIND_CODER:
		    final int readySize = socketChannelCtx.getWriteIndex() - socketChannelCtx.getCurrPackageIndex();
		    final int headLimit = 1024;
		    if (readySize >= headLimit) {
			throw new CoderException("未找到编/解码处理器 ");
		    }
		    run = false;

		    break;
		case UNFINISHED:
		case UNKNOWN:
		case ERROR:
		default:
		    run = false;
		    // TODO throw
		    break;
		}
	    }

	    if (count % 500 == 0) {
		System.out.println("readBuffer capacity :" + buffer.capacity());
		Thread.sleep(200);
	    }
	}
    }
}
