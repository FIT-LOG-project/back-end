package com.swoo.fitlog.exception;

public class IncorrectRefreshTokenException extends RuntimeException {
    public IncorrectRefreshTokenException() {
        super();
    }

    public IncorrectRefreshTokenException(String message) {
        super(message);
    }

    public IncorrectRefreshTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectRefreshTokenException(Throwable cause) {
        super(cause);
    }

    protected IncorrectRefreshTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
