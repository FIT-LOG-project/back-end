package com.swoo.fitlog.exception;

public class TokenSecurityException extends RuntimeException {
    public TokenSecurityException() {
        super();
    }

    public TokenSecurityException(String message) {
        super(message);
    }

    public TokenSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenSecurityException(Throwable cause) {
        super(cause);
    }

    protected TokenSecurityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
