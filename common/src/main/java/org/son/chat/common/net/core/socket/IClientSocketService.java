package org.son.chat.common.net.core.socket;

import java.nio.ByteBuffer;

/**
 * 客户端socket服务 抽象
 * @author solq
 */
public interface IClientSocketService {

	// //////////////////消息处理行为//////////////////////////
	public void send(Object message);

	public void send(ByteBuffer byteBuffer);
}
