package com.swoo.fitlog.exception;

public class NoMatchPasswordException extends RuntimeException {
    public NoMatchPasswordException() {
        super();
    }

    public NoMatchPasswordException(String message) {
        super(message);
    }

    public NoMatchPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchPasswordException(Throwable cause) {
        super(cause);
    }

    protected NoMatchPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
