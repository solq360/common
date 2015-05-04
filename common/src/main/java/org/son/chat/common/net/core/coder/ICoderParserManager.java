package org.son.chat.common.net.core.coder;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.coder.impl.CoderParser;
import org.son.chat.common.net.core.coder.impl.CoderResult;

/**
 * 编/码处理器管理
 * 
 * @author solq
 */
public interface ICoderParserManager {

    /**
     * 解码处理
     * 
     * @return CoderResult
     * */
    CoderResult decode(ByteBuffer buffer, ICoderCtx ctx);

    /**
     * 编码处理
     * */
    ByteBuffer encode(Object message, ICoderCtx ctx);

    void error(ByteBuffer buffer, ICoderCtx ctx);

    /** 注册 编/码处理器 */
    void register(CoderParser coderParser);
}
