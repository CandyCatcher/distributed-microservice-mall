package top.candysky.rabbit.api.exception;

import java.io.Serializable;

/**
 * 运行时的异常在这里抛出
 */
public class MessageRunTimeException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 8756005440567537061L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
