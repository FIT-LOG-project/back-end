package com.swoo.fitlog.api.enums;

import com.swoo.fitlog.utils.ErrorCodeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ErrorCodeUtilTest {

    @Test
    @DisplayName("에러코드 반환 성공")
    void getErrorCode() {
        // given
        int emailErrorCode = 10;

        // when
        int emailFieldErrorCode = ErrorCodeUtil.getFieldErrorCode("email");

        // then
        Assertions.assertEquals(emailErrorCode, emailFieldErrorCode);
    }
}