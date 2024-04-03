package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.exception.ExpiredPasswordException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PasswordAuthServiceTest {

    @Autowired
    PasswordAuthService passwordAuthService;

    @Test
    @DisplayName("재 확인 비밀 번호 인증 성공")
    void successReconfirmPassword() {
        // given
        String email = "test@example.com";
        String password = "123456";
        String reconfirmPassword = "123456";

        // when
        passwordAuthService.saveTemporaryPassword(email, password);

        // then
        assertThat(passwordAuthService.certifyPassword(email, reconfirmPassword)).isTrue();
    }

    @Test
    @DisplayName("재확인 비밀 번호 불일치")
    void failedReconfirmPassword() {
        // given
        String email = "test2@example.com";
        String password = "123456";
        String reconfirmPassword = "1234567";

        // when
        passwordAuthService.saveTemporaryPassword(email, password);

        // then
        assertThat(passwordAuthService.certifyPassword(email, reconfirmPassword)).isFalse();
    }

    @Test
    @DisplayName("만료된 비밀 번호")
    void expiredPassword() {
        // given
        String email = "test3@example.com";
        String reconfirmPassword = "1234567";

        // when
        // then
        assertThrows(ExpiredPasswordException.class,
                () -> passwordAuthService.certifyPassword(email, reconfirmPassword));
    }
}