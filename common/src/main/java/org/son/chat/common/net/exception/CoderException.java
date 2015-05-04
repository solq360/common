package org.son.chat.common.net.exception;

/**
 * 编/解码模块异常
 * 
 * @author solq
 */
public class CoderException extends RuntimeException {

    private static final long serialVersionUID = -5467773602825684956L;

    public CoderException(String message, Throwable cause) {
	super(message, cause);
    }

    public CoderException(Throwable cause) {
	super(cause);
    }

    public CoderException(String message) {
	super(message);
    }

    public CoderException() {
	super();
    }

}
