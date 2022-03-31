package org.nefure.nefurehouse.exception;

/**
 * 文件预览异常类
 * @author nefure
 */
public class PreviewException extends RuntimeException {

    private static final long serialVersionUID = 8663951517262898832L;

    public PreviewException() {
    }

    public PreviewException(String message) {
        super(message);
    }

    public PreviewException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreviewException(Throwable cause) {
        super(cause);
    }

    public PreviewException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}