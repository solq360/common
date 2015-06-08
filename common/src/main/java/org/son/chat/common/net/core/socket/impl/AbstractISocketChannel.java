package org.son.chat.common.net.core.socket.impl;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.son.chat.common.net.config.SocketChannelConfig;
import org.son.chat.common.net.core.coder.ICoderParserManager;
import org.son.chat.common.net.core.coder.impl.CoderResult;
import org.son.chat.common.net.core.handle.ISocketHandle;
import org.son.chat.common.net.core.socket.ISocketChannel;
import org.son.chat.common.net.core.socket.ISocketPool;
import org.son.chat.common.net.core.socket.ISocketService;
import org.son.chat.common.net.exception.CoderException;
import org.son.chat.common.net.exception.NetException;
import org.son.chat.common.net.util.NioUtil;

/**
 * socketchannel 模板
 * 
 * @author solq
 */
public abstract class AbstractISocketChannel implements ISocketChannel, ISocketService {

    protected boolean close = true;
    protected boolean init = false;

    protected ICoderParserManager coderParserManager;
    protected SocketChannelConfig socketChannelConfig;
    protected ISocketHandle handle;
    protected ISocketPool pool;

    /**
     * 读写分离 参考 : http://developer.51cto.com/art/201112/306532.htm <br>
     * 也可以参考netty
     */
    @Override
    public void start() {
	if (!init) {
	    init();
	}
	// TODO
	// while (!close) {
	// Selector selector = getSelector();
	// try {
	// // TODO 占用CPU 处理
	// // 参考 : http://xw-z1985.iteye.com/blog/1928244
	// // http://www.tuicool.com/articles/36zimq
	// // http://xw-z1985.iteye.com/blog/1748660
	// int n = selector .select(10);
	// if (n < 0) {
	// continue;
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// break;
	// }
	// for (Iterator<SelectionKey> i = selector.selectedKeys().iterator();
	// i.hasNext();) {
	// // 得到下一个Key
	// SelectionKey key = i.next();
	// try {
	// handle(key);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// i.remove();
	// }
	// }
	// stop();
    }

    @Override
    public void stop() {
	init = false;
	close = true;
	handle = null;
	socketChannelConfig = null;
	coderParserManager = null;
	// selector = null;
    }

    @Override
    public void doConnect(SelectionKey key) {
	System.out.println(" doConnect ");
	NioUtil.clearOps(key, SelectionKey.OP_CONNECT);
	NioUtil.setOps(key, SelectionKey.OP_READ);
    }

    @Override
    public void doClose(SelectionKey key) {
	System.out.println(" doClose ");
	this.stop();
    }

    @Override
    public void doAccept(SelectionKey key) {
	System.out.println(" doAccept ");
    }

    @Override
    public void doWrite(SelectionKey key) {
	System.out.println(" doWrite ");
	// 服务端读到东西后，注册写事件。等写完东西后取消写事件的注册。
	NioUtil.clearOps(key, SelectionKey.OP_WRITE);
    }

    @Override
    public void doRead(SelectionKey key) {
	System.out.println(" doRead ");
	final SocketChannel clientChannel = (SocketChannel) key.channel();
	final ClientSocket clientSocket = (ClientSocket) key.attachment();
	final SocketChannelCtx socketChannelCtx = clientSocket.getCtx();

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
	    key.cancel();
	    clientSocket.stop();
	    throw new NetException("读取Socket数据异常 : ", e);
	}

	// 编码处理
	if (bytesRead == -1) {
	    // 链路关闭，不清理读操作会造成死循环
	    NioUtil.clearOps(key, SelectionKey.OP_READ);
	    coderParserManager.error(buffer, socketChannelCtx);
	    key.cancel();
	    clientSocket.stop();
	} else {
	    boolean run = true;
	    // 粘包处理
	    while (run) {
		ByteBuffer cpbuffer = socketChannelCtx.coderBegin();
		cpbuffer.mark();
		CoderResult coderResult = coderParserManager.decode(cpbuffer, socketChannelCtx);
		switch (coderResult.getValue()) {
		case SUCCEED:
		    break;
		case NOT_FIND_CODER:
		    final int readySize = socketChannelCtx.getWriteIndex() - socketChannelCtx.getCurrPackageIndex();
		    final int headLimit = 255;
		    if (readySize >= headLimit) {
			throw new CoderException("未找到编/解码处理器 ");
		    }
		    run = false;
		    break;
		case UNFINISHED:
		case UNKNOWN:
		case ERROR:
		default:
		    run = false;
		    // TODO throw
		    break;
		}
	    }
	}
    }

    // getter
    @Override
    public SocketChannelConfig getSocketChannelConfig() {
	return socketChannelConfig;
    }

    @Override
    public void setCoderParserManager(ICoderParserManager coderParserManager) {
	this.coderParserManager = coderParserManager;
    }
    
    @Override
    public boolean isClose() {
	return close;
    }

    public boolean isInit() {
	return init;
    }

    public ISocketHandle getHandle() {
	return handle;
    }

    @Override
    public ISocketPool getPool() {
	return this.pool;
    }

    public void register(ISocketPool pool) {
	this.pool = pool;
    }

    public Selector getSelector() {
	return this.pool.getSelector();
    }
}
