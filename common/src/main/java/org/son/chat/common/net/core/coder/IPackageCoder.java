package org.son.chat.common.net.core.coder;

/**
 * @author solq
 */
public interface IPackageCoder<INPUT, OUT> extends ICoder<INPUT, OUT> {
	/** 包验证 */
	public boolean verify(OUT value,IcoderCtx ctx);
}
