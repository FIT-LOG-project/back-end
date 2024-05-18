package com.swoo.fitlog.exception;

public class DuplicatedNickname extends RuntimeException {
    public DuplicatedNickname() {
        super();
    }

    public DuplicatedNickname(String message) {
        super(message);
    }

    public DuplicatedNickname(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedNickname(Throwable cause) {
        super(cause);
    }

    protected DuplicatedNickname(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
