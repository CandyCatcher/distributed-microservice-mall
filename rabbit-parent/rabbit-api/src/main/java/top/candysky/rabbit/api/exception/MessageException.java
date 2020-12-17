package top.candysky.rabbit.api.exception;

import java.io.Serializable;

/**
 * 一些初始化的异常在这里抛出
 * 其实还可以具体到更多的异常
 */
public class MessageException extends Exception implements Serializable{

    private static final long serialVersionUID = -1403834677340236095L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
