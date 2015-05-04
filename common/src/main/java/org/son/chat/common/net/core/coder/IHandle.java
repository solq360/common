package org.son.chat.common.net.core.coder;

/**
 * @author solq
 */
public interface IHandle<INPUT, OUT> extends ICoder<INPUT, OUT> {
    /** 回写验证 */
    public boolean verify(Object value, ICoderCtx ctx);

    /** 业务处理 */
    public void handle(INPUT value, ICoderCtx ctx);

}
