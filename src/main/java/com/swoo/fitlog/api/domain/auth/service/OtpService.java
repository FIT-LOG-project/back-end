package com.swoo.fitlog.api.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private static final Long EXPIRATION_TIME_MINUTES = 5L; // 유효 시간 5분

    private final RedisTemplate<String, Integer> redisTemplate;

    /*
     * 인증 번호 생성하고 저장
     *
     * 1. 인증 번호 생성 메서드 호출
     * 2. email과 "OTP:" 문자열을 조합하여 KEY 생성
     * 3. redis에 KEY:OTP 저장
     * 4. 인증 번호 반환
     * */
    public int generateAndSaveOTP(String email) {
        int otp = generateOTP();
        String key = createKey(email);

        redisTemplate.opsForValue().set(key, otp, EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);

        log.debug("[KEY][{}]", redisTemplate.opsForValue().get(key));
        return otp;
    }

    /*
     * key 생성
     * */
    private String createKey(String email) {
        return "OTP:" + email;
    }

    /*
     * 인증 번호 생성
     * */
    private int generateOTP() {
        Random random = new Random();

        return 100000 + random.nextInt(900000);
    }
}
