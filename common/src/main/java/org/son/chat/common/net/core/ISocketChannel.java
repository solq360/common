package org.son.chat.common.net.core;

/**
 * 顶级接口抽象原则，必须的处理行为。细分的行为/属性不做抽象声明 <br>
 * socket 管理接口抽象 针对两socket 绑定处理
 * @author solq
 */
public interface ISocketChannel {

	// ////////////////配置属性////////////////////////
	public SocketChannelConfig getSocketChannelConfig();

	public SocketChannelCtx getSocketChannelCtx();

	// //////////////////链路处理行为//////////////////////////
	public void open();

	public void register();

	public void close();

	public void unRegister();

}
