package org.son.chat.common.net.core;

/**
 * 客户端socket服务 抽象
 * @author solq
 */
public interface IClientSocketService extends ISocketService {
	// //////////////////消息处理行为//////////////////////////
	public byte[] read();

	public void send(byte[] msg);
}
