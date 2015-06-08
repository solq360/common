package org.son.chat.common;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.son.chat.common.net.core.socket.ISocketChannel;
import org.son.chat.common.net.util.NioUtil;

/**
 * 
 * @author solq
 */
public class TestSelector {

    /***
     * Selector: 获取键集 Set<SelectionKey> keys() <br>
     * Set<SelectionKey> selectedKeys() <br>
     * 以上方法返回选择器的不同键集。keys()方法返回当前已注册的所有键。 <br>
     * 返回的键集是不可修改的：任何对其进行直接修改的尝试（如，调用其remove()方法）
     * 都将抛出UnsupportedOperationException异常。 <br>
     * selectedKeys()方法用于返回上次调用select()方法时，被"选中"的已准备好进行I/O操作的键。 <br>
     * 重要提示：selectedKeys()方法返回的键集是可修改的，实际上在两次调用select()方法之间，都必须"手工"将其清空。 <br>
     * 换句话说，select方法只会在已有的所选键集上添加键，它们不会创建新的键集。<br>
     * @throws InterruptedException 
     */
    @Test
    public void testResigter() throws IOException, InterruptedException {
	final Selector selector = Selector.open();
	Selector selector2 = Selector.open();


	ServerSocketChannel socketChannel = ServerSocketChannel.open();
	socketChannel.configureBlocking(false);
	socketChannel.bind(new InetSocketAddress(6969));
	socketChannel.register(selector, SelectionKey.OP_ACCEPT);
	new Thread(new Runnable() {

	    @Override
	    public void run() {
		while (true) {
		    try {
			int n = selector.select();
			if (n <= 0) {
			    System.out.println("xxxxxxxxxxxxx");
			    continue;
			}
			for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
			    // 得到下一个Key
			    SelectionKey key = i.next();

			    // 要手动移除 SelectionKey
			    i.remove();
			    NioUtil.printlnOps(key);
			}
		    } catch (IOException e) {
			e.printStackTrace();
		    }

		}

	    }
	}).start();
	//socketChannel.register(selector2, SelectionKey.OP_ACCEPT);
	while(true){
	    Thread.yield();
	}
    }

    @Test
    public void testResigter1() throws IOException {
	Selector selector = Selector.open();
	SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(6969));
	socketChannel.configureBlocking(false);
	socketChannel.register(selector, 0);

	while (true) {
	    int n = selector.select();
	    if (n < 0) {
		System.out.println(n);
		continue;
	    }
	    for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
		// 得到下一个Key
		SelectionKey key = i.next();

		// 要手动移除 SelectionKey
		i.remove();
		NioUtil.printlnOps(key);
	    }
	}

    }

    // 关服时执行 SelectionKey.cancel()
    // 关闭时 或者read 操作异常 也要执行

    /***
     * 通过Selector选择通道 <br>
     * 一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）
     * 已经准备就绪的那些通道。 <br>
     * 换句话说，如果你对“读就绪”的通道感兴趣，select()方法会返回读事件已经就绪的那些通道。 <br>
     * 下面是select()方法：（该方法是阻塞方法） <br>
     * int select() <br>
     * int select(long timeout) <br>
     * int selectNow() <br>
     * select()阻塞到至少有一个通道在你注册的事件上就绪了。 <br>
     * select(long timeout)和select()一样，除了最长会阻塞timeout毫秒(参数)。
     */
    /***
     * Selector.open();占用双TCP <br>
     * http://developer.51cto.com/art/201112/306870.htm <br>
     * http://zhhphappy.iteye.com/blog/2032893
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testSelector() throws IOException, InterruptedException {
	int count = 10;
	for (int i = 0; i < count; i++) {
	    Selector.open();
	}
	Thread.sleep(30000);
    }

    @Test
    public void testCompareAndSet() {
	AtomicBoolean wakenUp = new AtomicBoolean();
	wakenUp.set(true);
	boolean result = wakenUp.compareAndSet(false, true);
	System.out.println("one : " + result);

	wakenUp.set(false);
	result = wakenUp.compareAndSet(false, true);
	System.out.println("two : " + result);
    }
}
