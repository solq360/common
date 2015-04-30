package org.son.chat.common;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals(3, bb.get(2));
	}

	@Test
	public void testSharedBuffer(){
		ByteBuffer bb = ByteBuffer.allocate(50);
		bb.put((byte) 1);
		bb.put((byte) 2);
		bb.put((byte) 3);
		
		ByteBuffer cloneBB=bb.asReadOnlyBuffer();
		bb.put((byte) 4);
		
		Assert.assertEquals(4, cloneBB.get(3));
		Assert.assertEquals(4, cloneBB.get(3));

		cloneBB.position(3);
		bb.position(5);
		Assert.assertEquals(4, cloneBB.get());

	}
}
