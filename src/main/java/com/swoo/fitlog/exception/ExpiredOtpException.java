package com.swoo.fitlog.exception;

public class ExpiredOtpException extends RuntimeException{
    public ExpiredOtpException() {
        super();
    }

    public ExpiredOtpException(String message) {
        super(message);
    }

    public ExpiredOtpException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredOtpException(Throwable cause) {
        super(cause);
    }

    protected ExpiredOtpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
