package org.son.chat.common.net.core.handle;

import java.util.LinkedList;

import org.son.chat.common.net.core.coder.ICoderCtx;

/**
 * @author solq
 * */
public class PipeHandle extends AbstractSocketHandle {

    private LinkedList<ISocketHandle> pipe = new LinkedList<>();

    public synchronized void register(ISocketHandle... handles) {
	for (ISocketHandle handle : handles) {
	    pipe.add(handle);
	}
    }

    @Override
    public void openBefore(ICoderCtx ctx) {
	for (ISocketHandle handle : pipe) {
	    handle.openBefore(ctx);
	}
    }

    @Override
    public void openAfter(ICoderCtx ctx) {
	for (ISocketHandle handle : pipe) {
	    handle.openAfter(ctx);
	}
    }

    @Override
    public void closeBefore(ICoderCtx ctx) {
	for (ISocketHandle handle : pipe) {
	    handle.closeBefore(ctx);
	}
    }

    @Override
    public void closeAfter(ICoderCtx ctx) {
	for (ISocketHandle handle : pipe) {
	    handle.closeAfter(ctx);
	}
    }

    @Override
    public void readBefore(ICoderCtx ctx, Object request) {
	for (ISocketHandle handle : pipe) {
	    handle.readBefore(ctx, request);
	}
    }

    @Override
    public void readAfter(ICoderCtx ctx, Object request) {
	for (ISocketHandle handle : pipe) {
	    handle.readAfter(ctx, request);
	}
    }

    @Override
    public void writeBefore(ICoderCtx ctx, Object response) {
	for (ISocketHandle handle : pipe) {
	    handle.writeBefore(ctx, response);
	}
    }

    @Override
    public void writeAfter(ICoderCtx ctx, Object response) {
	for (ISocketHandle handle : pipe) {
	    handle.writeAfter(ctx, response);
	}
    }

    @Override
    public void openError(ICoderCtx ctx) {
	for (ISocketHandle handle : pipe) {
	    handle.openError(ctx);
	}
    }

    @Override
    public void writeError(ICoderCtx ctx, Object response) {
	for (ISocketHandle handle : pipe) {
	    handle.writeError(ctx, response);
	}
    }
}
