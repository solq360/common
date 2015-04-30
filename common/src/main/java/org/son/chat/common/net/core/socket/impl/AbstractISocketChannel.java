package org.son.chat.common.net.core.socket.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderResult;
import org.son.chat.common.net.core.socket.ISocketChannel;
import org.son.chat.common.net.core.socket.ISocketService;
import org.son.chat.common.net.exception.CoderException;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;

/**
 * socketchannel 模板
 * @author solq
 */
public abstract class AbstractISocketChannel implements ISocketChannel,ISocketService {

	/** jdk selector **/
	protected Selector selector;

	protected boolean close = true;

	protected ICoderParserManager coderParserManager;
	protected SocketChannelConfig socketChannelConfig;

	// protected ISocketHandle socketHandle;

	@Override
	public SocketChannelConfig getSocketChannelConfig() {
		return socketChannelConfig;
	}

	@Override
	public void setCoderParserManager(ICoderParserManager coderParserManager) {
		this.coderParserManager = coderParserManager;
	}

	@Override
	public void open() {
		// socketHandle.open(socketChannelCtx);
	}

	@Override
	public void register() {
		// socketHandle.register(socketChannelCtx);
	}

	@Override
	public void close() {
		// socketHandle.close(socketChannelCtx);
	}

	@Override
	public void unRegister() {
		// socketHandle.unRegister(socketChannelCtx);
	}
	
	
	@Override
	public void start() {
		init();
		while (!close) {
			try {
				int n = selector.select(10);
				if (n < 0) {
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
				// 得到下一个Key
				SelectionKey key = i.next();
				try {
					handle(key);
				} catch (Exception e) {
					e.printStackTrace();
				}
				i.remove();
			}
		}
		stop();
	}

	protected void handle(SelectionKey key) {
		if (!key.isValid()) {
			System.out.println("error key");
			unRegister();
			close();
			return;
		}
		// 可能存在一次在channel 注册多个操作 不能用 elseif
		try {
			if (key.isAcceptable()) {
				handleAccept(key);
			}
			if (key.isConnectable()) {
				handleConnected(key);
			}
			if (key.isReadable()) {
				handleRead(key);
			}
			if (key.isWritable()) {
				handleWrite(key);
			}
		} catch (CancelledKeyException e) { // key 消除事件
			e.printStackTrace();
			unRegister();
			close();
		}
	}

	void handleAccept(SelectionKey key) {
		System.out.println(" handleAccept ");
		try {
			SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
			clientChannel.configureBlocking(false);
			SocketChannelCtx ctx = SocketChannelCtx.valueOf(selector, clientChannel, this.coderParserManager);			
			// 必须是新注册的 SelectionKey
			SelectionKey sk = clientChannel.register(selector, 0, ctx);
			ctx.setSelectionKey(sk);
			NioUtil.setOps(sk, SelectionKey.OP_READ);
			open();
		} catch (IOException e) {
			throw new NetException("Socket连接异常 : ", e);
		}
	}

	void handleConnected(SelectionKey key) {
		System.out.println(" handleConnected ");
		open();
		NioUtil.clearOps(key, SelectionKey.OP_CONNECT);
		NioUtil.setOps(key, SelectionKey.OP_READ);
	}

	void handleWrite(SelectionKey key) {
		System.out.println(" handleWrite ");
		// 服务端读到东西后，注册写事件。等写完东西后取消写事件的注册。
		NioUtil.clearOps(key, SelectionKey.OP_WRITE);
	}

	void handleRead(SelectionKey key) {
		System.out.println(" handleRead ");
		SocketChannel clientChannel = (SocketChannel) key.channel();
		SocketChannelCtx socketChannelCtx = (SocketChannelCtx) key.attachment();
		ByteBuffer buffer = null;
		long bytesRead = -1;
		try {
			buffer = socketChannelCtx.readBegin();
			bytesRead = clientChannel.read(buffer);
			socketChannelCtx.readEnd(bytesRead);

		} catch (Exception e) {
			// 链路关闭，不清理读操作会造成死循环
			NioUtil.clearOps(key, SelectionKey.OP_READ);
			coderParserManager.error(buffer, socketChannelCtx);
			try {
				clientChannel.close();
			} catch (IOException e1) {
				throw new NetException("关闭Socket异常 : ", e1);
			}
			throw new NetException("读取Socket数据异常 : ", e);
		}

		// 编码处理
		try {
			if (bytesRead == -1) {
				coderParserManager.error(buffer, socketChannelCtx);
			} else {
				boolean run = true;
				//粘包处理
				while(run){
					CoderResult coderResult = coderParserManager.decode(buffer, socketChannelCtx);
					switch (coderResult.getValue()) {
					case SUCCEED:
 						break;
					case NOT_FIND_CODER:
						final int readySize = socketChannelCtx.getWriteIndex() -socketChannelCtx.getCurrPackageIndex();
						final int headLimit = 1024;
						if( readySize>= headLimit ){
							throw new CoderException("未找到编/解码处理器 ");
						}
						run=false;

						break;
					case UNFINISHED: 
 					case UNKNOWN:
					case ERROR:
					default:
						run=false;
						// TODO throw
						break;
					}
					
					
				}
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
