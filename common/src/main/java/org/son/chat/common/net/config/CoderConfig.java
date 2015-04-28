package org.son.chat.common.net.config;

import org.son.chat.common.net.core.coder.ICoder;
import org.son.chat.common.net.handle.ISocketHandle;

/**
 * 编/解码流程控制 注意优先级
 * @author solq
 */
public enum CoderConfig {

	CHAT(null);

	/** 业务处理分发 **/
	private ISocketHandle socketHandle;

	/** 链式编/解码 **/
	private ICoder<?, ?>[] coders;

	private CoderConfig(ISocketHandle socketHandle, ICoder<?, ?>... coders) {
		this.socketHandle = socketHandle;
		this.coders = coders;
	}

	// getter

	public ISocketHandle getSocketHandle() {
		return socketHandle;
	}

	public ICoder<?, ?>[] getCoders() {
		return coders;
	}
}
