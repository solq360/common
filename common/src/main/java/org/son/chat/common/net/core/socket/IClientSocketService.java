package org.son.chat.common.net.core.socket;

import java.nio.ByteBuffer;

import org.son.chat.common.net.core.handle.ISocketHandle;

/**
 * 客户端socket服务 抽象
 * 
 * @author solq
 */
public interface IClientSocketService extends ISocketHandle {

    public boolean isClose();

    public boolean isConected();

    // //////////////////消息处理行为//////////////////////////
    public void send(Object message);

    public void send(Object message, ByteBuffer byteBuffer);
}
