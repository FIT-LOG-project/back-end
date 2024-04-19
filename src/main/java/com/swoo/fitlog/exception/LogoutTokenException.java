package com.swoo.fitlog.exception;

public class LogoutTokenException extends RuntimeException {
    public LogoutTokenException() {
        super();
    }

    public LogoutTokenException(String message) {
        super(message);
    }

    public LogoutTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogoutTokenException(Throwable cause) {
        super(cause);
    }

    protected LogoutTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
