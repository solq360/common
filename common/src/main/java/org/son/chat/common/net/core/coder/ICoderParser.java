package org.son.chat.common.net.core.coder;

import java.nio.ByteBuffer;

/**
 * 编码解析器
 * @author solq
 */
public interface ICoderParser {

	CoderResult decode(ByteBuffer buffer);

	Object encode(Object message);

	void error(ByteBuffer buffer);
}
