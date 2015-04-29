package org.son.chat.common.net.core.socket;

/**
 * 客户端socket服务 抽象
 * @author solq
 */
public interface IClientSocketService extends ISocketService {
	// //////////////////消息处理行为//////////////////////////
	public void send(Object message);
}
