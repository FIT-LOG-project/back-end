package com.swoo.fitlog.exception;

public class ExpiredPasswordException extends RuntimeException {
    public ExpiredPasswordException() {
        super();
    }

    public ExpiredPasswordException(String message) {
        super(message);
    }

    public ExpiredPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredPasswordException(Throwable cause) {
        super(cause);
    }

    protected ExpiredPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
