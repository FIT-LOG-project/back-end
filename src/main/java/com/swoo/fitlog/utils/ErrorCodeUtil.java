package com.swoo.fitlog.utils;

import lombok.Getter;

@Getter
public enum ErrorCodeUtil {

    NOT_EXIST_MEMBER(1),
    EMAIL(10),
    EMAIL_DUPLICATE(11),
    PASSWORD(20),
    PASSWORD_EXPIRED_TIME(21),
    PASSWORD_FAIL_AUTH(22),
    PASSWORD_NEW_NOT_AGREEMENT(23),
    FAILED_LOGIN(30),
    ACCESS_TOKEN_EXPIRED(62),
    INCORRECT_TOKEN(63),
    ACCESS_TOKEN_ALREADY_LOGOUT(64),
    REQUEST_LOGIN(65),
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
