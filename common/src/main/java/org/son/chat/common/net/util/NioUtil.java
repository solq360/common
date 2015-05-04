package org.son.chat.common.net.util;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author solq
 */
public abstract class NioUtil {
    public static void registerSelectionKey(SelectionKey key, Selector selector, int ops) {
	SocketChannel channel = (SocketChannel) key.channel();
	try {
	    channel.register(selector, ops);
	} catch (ClosedChannelException e) {
	    e.printStackTrace();
	}
    }

    public static void registerSelectionKey(SocketChannel channel, Selector selector, int ops) {
	try {
	    channel.register(selector, ops);
	} catch (ClosedChannelException e) {
	    e.printStackTrace();
	}
    }

    /**
     * @param key
     * @param ops
     *            {@link SelectionKey}
     */
    public static void setOps(SelectionKey key, int... ops) {
	final int interestOps = key.interestOps();
	for (int op : ops) {
	    if ((interestOps & op) == 0) {
		key.interestOps(interestOps | op);
	    }
	}
    }

    /***
     * 清除操作标志，让选择器不做处理
     */
    public static void clearOps(SelectionKey key, int... ops) {
	final int interestOps = key.interestOps();
	for (int op : ops) {
	    if ((interestOps & op) != 0) {
		key.interestOps(interestOps & ~op);
	    }
	}
    }

    public static void setOpWrite(SelectionKey key) {
	setOps(key, SelectionKey.OP_WRITE);
    }

    public static void clearOpWrite(SelectionKey key) {
	clearOps(key, SelectionKey.OP_WRITE);
    }

    public static void printlnOps(SelectionKey key) {
	final boolean acceptable = key.isAcceptable();
	final boolean connectable = key.isConnectable();
	final boolean readable = key.isReadable();
	final boolean writable = key.isWritable();
	System.out.println("acceptable : " + acceptable + " connectable : " + connectable + " readable : " + readable + " writable : " + writable);
    }
}
