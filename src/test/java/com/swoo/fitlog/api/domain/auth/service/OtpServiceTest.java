package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.exception.ExpiredOtpException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OtpServiceTest {


    @Autowired
    private OtpService otpService;

    @Test
    @DisplayName("인증 번호 생성하고 인증 성공")
    void successCreateOtp() {
        // given
        String mail = "test@example.com";

        // then
        int otp = otpService.generateAndSaveOTP(mail);
        System.out.println(otp);

        // when
        assertTrue(otpService.verifyOTP(mail, otp));
    }

    @Test
    @DisplayName("인증 번호 만료 또는 존재하지 않는 키일 경우")
    void expiredTime() {
        // given
        String mail = "test2@example.com";

        // when
        // then
        assertThrows(ExpiredOtpException.class, () -> otpService.verifyOTP(mail, 123456));
    }

    @Test
    @DisplayName("일치 하지 않는 인증 번호")
    void notAgreement() {
        // given
        String mail = "test@example.com";
        int notAgreementOtp = 111111;

        // when
        int otp = otpService.generateAndSaveOTP(mail);

        // otpService에서 생성한 인증 번호가 111111일 경우 notAgreementOtp와 같아지기 때문에 다른 값을 설정해준다.
        if (otp == notAgreementOtp) {
            notAgreementOtp = 111112;
        }

        // then
        assertFalse(otpService.verifyOTP(mail, notAgreementOtp));
    }

    @Test
    @DisplayName("인증 번호 남은 시간 조회")
    void checkOtpExpireTime() {
        // given
        String mail = "test@example.com";

        // when
        otpService.generateAndSaveOTP(mail);

        // then
        Long expiration = otpService.getExpiration(mail);
        assertTrue(expiration > 0);
    }
}