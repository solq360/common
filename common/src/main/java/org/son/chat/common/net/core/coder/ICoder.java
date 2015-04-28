package org.son.chat.common.net.core.coder;

/**
 * 编/解码处理器
 * @author solq
 */
public interface ICoder<INPUT, OUT> {

	/** 编码 */
	public OUT encode(INPUT input);

	/** 解码 */
	public INPUT decode(OUT out);

}
