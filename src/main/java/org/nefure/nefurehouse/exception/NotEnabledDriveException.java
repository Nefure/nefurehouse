package org.nefure.nefurehouse.exception;

/**
 * 未启用的驱动器异常
 * @author nefure
 */
public class NotEnabledDriveException extends RuntimeException {

    private static final long serialVersionUID = -8366435949706994502L;

    public NotEnabledDriveException() {
    }

    public NotEnabledDriveException(String message) {
        super(message);
    }

    public NotEnabledDriveException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnabledDriveException(Throwable cause) {
        super(cause);
    }

    public NotEnabledDriveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}