package org.son.chat.common.net.core.socket;

import org.son.chat.common.net.core.coder.ICoderCtx;

/**
 * socket 链路逻辑处理
 * 
 * @author solq
 */
public interface ISocketHandle {
    void open(ICoderCtx ctx);

    void register(ICoderCtx ctx);

    void close(ICoderCtx ctx);

    void unRegister(ICoderCtx ctx);
}
