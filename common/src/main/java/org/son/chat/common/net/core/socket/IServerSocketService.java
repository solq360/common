package org.son.chat.common.net.core.socket;

import java.nio.ByteBuffer;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.socket.impl.ClientSocket;

/**
 * 服务端 socket 服务接口
 * @author solq
 */
public interface IServerSocketService {

	public void sendAll(Object message);
	public void send(String channelName,Object message);
	public void send(ClientSocket clientSocket,Object message);
	public void send(ClientSocket clientSocket,ByteBuffer byteBuffer);

	public void registerClientSocket(SocketChannelConfig config);
}
