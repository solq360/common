package org.son.chat.common.net.core.handle;

import org.son.chat.common.net.core.coder.ICoderCtx;

/**
 * socket 链路逻辑处理
 * 
 * @author solq
 */
public interface ISocketHandle {
    /**
     * 建立连接之前
     * */
    void openBefore(ICoderCtx ctx);

    /**
     * 成功建立连接之后
     * */
    void openAfter(ICoderCtx ctx);

    /**
     * 建立连接失败
     * */
    void openError(ICoderCtx ctx);

    /**
     * 关闭连接之前
     * */
    void closeBefore(ICoderCtx ctx);

    /**
     * 关闭连接之后
     * */
    void closeAfter(ICoderCtx ctx);

    /**
     * socket read 之后，编码之前
     * */
    void readBefore(ICoderCtx ctx, Object request);

    /**
     * socket read 之后，编码之后
     * */
    void readAfter(ICoderCtx ctx, Object request);

    /**
     * socket write 之后，编码之前
     * */
    void writeBefore(ICoderCtx ctx, Object response);

    /**
     * socket write 之后，编码之后
     * */
    void writeAfter(ICoderCtx ctx, Object response);

    /**
     * socket write 失败
     * */
    void writeError(ICoderCtx ctx, Object response);
}
