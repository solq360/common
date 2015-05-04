package org.son.chat.common.net.core.socket;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.coder.ICoderCtx;

/**
 * 客户端socket服务 抽象
 * 
 * @author solq
 */
public interface IClientSocketService {

    public boolean isClose();

    public boolean isConected();

    // //////////////////消息处理行为//////////////////////////
    public void send(Object message);

    public void send(ByteBuffer byteBuffer);

    // //////////////////链路处理行为//////////////////////////
    public void open(ICoderCtx ctx);

    public void register(ICoderCtx ctx);

    public void close(ICoderCtx ctx);

    public void unRegister(ICoderCtx ctx);

}
