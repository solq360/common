package org.son.chat.common.net.exception;

/**
 * 网络模块异常
 * 
 * @author solq
 */
public class NetException extends RuntimeException {

    private static final long serialVersionUID = -8019221448806371216L;

    public NetException(String message, Throwable cause) {
	super(message, cause);
    }

    public NetException(Throwable cause) {
	super(cause);
    }

    public NetException(String message) {
	super(message);
    }

    public NetException() {
	super();
    }

}
