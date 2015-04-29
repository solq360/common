package org.son.chat.common.net.core.coder;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderResult;

/**
 * 编/码处理器管理
 * @author solq
 */
public interface ICoderParserManager {

	CoderResult decode(ByteBuffer buffer,IcoderCtx ctx);

	ByteBuffer encode(Object message,IcoderCtx ctx);

	void error(ByteBuffer buffer,IcoderCtx ctx);

	/** 注册  编/码处理器*/
	void register(CoderParser coderParser);
}
