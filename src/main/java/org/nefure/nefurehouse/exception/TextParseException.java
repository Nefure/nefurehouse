package org.nefure.nefurehouse.exception;

/**
 * 文件解析异常
 * @author nefure
 */
public class TextParseException extends RuntimeException {

    private static final long serialVersionUID = -8003325072471209763L;

    public TextParseException() {
        super();
    }

    public TextParseException(String message) {
        super(message);
    }

    public TextParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextParseException(Throwable cause) {
        super(cause);
    }

    protected TextParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
