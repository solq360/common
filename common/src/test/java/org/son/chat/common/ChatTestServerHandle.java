package org.son.chat.common;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.son.chat.common.net.core.coder.IHandle;
import org.son.chat.common.net.core.coder.IcoderCtx;
import org.son.chat.common.net.core.socket.impl.SocketChannelCtx;

/**
 * 简单聊天业务处理
 * @author solq
 */
public class ChatTestServerHandle implements IHandle<String, byte[]> {

	@Override
	public byte[] encode(String value, IcoderCtx ctx) {
		return value.getBytes();
	}

	@Override
	public String decode(byte[] value, IcoderCtx ctx) {
		return new String(value);
	}

	@Override
	public boolean verify(Object value, IcoderCtx ctx) {
		// 测试不做验证
		return true;
	}

	@Override
	public void handle(String value, IcoderCtx ctx) {
		System.out.println("接收到信息 : ");
		final SocketChannelCtx socketChannelCtx = (SocketChannelCtx) ctx;
		// 回写自己处理
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			private int sendCount = 0;

			@Override
			public void run() {
				sendCount++;
				for (int i = 0; i < 10; i++) {
					socketChannelCtx.send(" 发送数据 : " + i + " 次数  : " + sendCount);
				}
				System.out.println(" push message");
			}
		}, new Date(), 5000L);

	}

}
