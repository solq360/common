package org.son.chat.common.net.core.socket;

import java.nio.ByteBuffer;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.handle.ISocketHandle;
import org.son.chat.common.net.core.session.ISession;
import org.son.chat.common.net.core.socket.impl.ClientSocket;

/**
 * 服务端 socket 服务接口
 * 
 * @author solq
 */
public interface IServerSocketService {

    // /////////////// 客户端发送消息处理////////////////////////
    public void sendAll(Object message);

    public void send(String channelName, Object message);

    public void send(ClientSocket clientSocket, Object message);

    public void send(ClientSocket clientSocket, Object message, ByteBuffer byteBuffer);

    // ///////////////客户端注册处理////////////////////////

    public ClientSocket registerClient(SocketChannelConfig config);

    public ISession createSession();

    public ISocketPool getNextPool();

    public ISocketPool[] getGroupPool();

    // /////////////// 监控处理////////////////////////

    public void registerHandle(ISocketHandle... handleArray);

}
