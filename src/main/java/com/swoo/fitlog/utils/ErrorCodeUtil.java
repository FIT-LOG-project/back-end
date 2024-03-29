package com.swoo.fitlog.utils;

import lombok.Getter;

@Getter
public enum ErrorCodeUtil {

    EMAIL(10),
    PASSWORD(20),
    OTP(70),
    OTP_EXPIRED_TIME(71),
    OTP_NOT_AGREEMENT(72);

    private final int errorCode;

    ErrorCodeUtil(int errorCode) {
        this.errorCode = errorCode;
    }

    public static int getFieldErrorCode(String fieldName) {
        ErrorCodeUtil errorCodeUtil = ErrorCodeUtil.valueOf(fieldName.toUpperCase());

        return errorCodeUtil.errorCode;
    }
}
