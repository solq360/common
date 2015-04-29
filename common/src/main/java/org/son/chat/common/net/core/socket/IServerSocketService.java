package org.son.chat.common.net.core.socket;

/**
 * 服务端 socket 服务接口
 * @author solq
 */
public interface IServerSocketService extends ISocketService {

	public void sendAll(byte[] msg);
}
