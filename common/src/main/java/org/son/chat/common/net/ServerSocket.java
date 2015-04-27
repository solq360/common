package org.son.chat.common.net;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.son.chat.common.net.core.AbstractISocketChannel;
import org.son.chat.common.net.core.IServerSocketService;

/**
 * 服务端socket 是一个管理多个 客户端 socket 处理 <br>
 * 客户端socket 是一个对一个 socket 处理 <br>
 * 所以我认为 服务端socket其实是维护管理 多个 客户端 socket 这样就大量简化编写 <br>
 * @author solq
 */
public class ServerSocket extends AbstractISocketChannel implements IServerSocketService {

	/** 已连接的客户端 */
	private Map<String, ClientSocket> clients = new ConcurrentHashMap<>();

	@Override
	public void init() {
 
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void sendAll(byte[] msg) {
		for (ClientSocket s : clients.values()) {
			s.send(msg);
		}
	}

}
