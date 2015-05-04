package org.son.chat.common.net.core.coder;

/**
 * 编/解码处理器
 * 
 * @author solq
 */
public interface ICoder<INPUT, OUT> {

    /** 编码 */
    public OUT encode(INPUT value, ICoderCtx ctx);

    /** 解码 */
    public INPUT decode(OUT value, ICoderCtx ctx);

}
