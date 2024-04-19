package com.swoo.fitlog.exception;

public class ReissueAccessTokenException extends RuntimeException {
    public ReissueAccessTokenException() {
        super();
    }

    public ReissueAccessTokenException(String message) {
        super(message);
    }

    public ReissueAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReissueAccessTokenException(Throwable cause) {
        super(cause);
    }

    protected ReissueAccessTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
