package org.son.chat.common.net.core.coder;

import java.nio.ByteBuffer;

/**
 * @author solq
 */
public class CoderParser implements ICoderParser {

	@Override
	public CoderResult decode(ByteBuffer buffer) {
		System.out.println("decode //////////////");
		return CoderResult.SUCCEED();
	}

	@Override
	public Object encode(Object message) {
		return null;
	}

	@Override
	public void error(ByteBuffer buffer) {

	}
}
