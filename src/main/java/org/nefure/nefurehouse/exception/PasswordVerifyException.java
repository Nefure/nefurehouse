package org.nefure.nefurehouse.exception;

/**
 * 密码校验失败异常
 * @author nefure
 */
public class PasswordVerifyException extends RuntimeException {

    private static final long serialVersionUID = 1509497529714213795L;

    public PasswordVerifyException() {
    }

    public PasswordVerifyException(String message) {
        super(message);
    }

    public PasswordVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PasswordVerifyException(Throwable cause) {
        super(cause);
    }

    public PasswordVerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}