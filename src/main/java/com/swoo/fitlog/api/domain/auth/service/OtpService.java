package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.exception.ExpiredOtpException;
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

    /**
     * 인증 번호의 유효성을 검사한다.
     *
     * @param email 인증 번호를 전송한 이메일
     * @param otp 이메일에 전송된 인증 번호
     * @return true - 인증 번호가 일치하면 true를 반환한다.<br>
     * false - 인증 번호가 일치하지 않으면 false를 반환한다.
     * @throws ExpiredOtpException 매개 변수로 받은 otp가 만료된 인증 번호라면 해당 예외를 던진다.
     * */
    public boolean verifyOTP(String email, int otp) {
        String key = createKey(email);

        Integer savedOTP = redisTemplate.opsForValue().get(key);

        /*
         * 반환된 인증 번호가 null이라면 만료된 인증 번호로 간주하고 ExpiredOtpException 예외 던지기
         * */
        if (savedOTP == null) {
            throw new ExpiredOtpException("만료된 인증 번호");
        }

        if (savedOTP == otp) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    /**
     * 인증 번호의 남은 시간을 조회한다.
     * @param email 인증 번호를 찾기 위해 Key로 사용되는 이메일
     * @return 분을 초로 변환한 값을 반환한다.  예) 5분 = 300초
     * @throws ExpiredOtpException 이미 만료된 인증 번호라면 해당 예외를 던진다.
     */
    public Long getExpiration(String email) {
        String key = createKey(email);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        if (expire == null || expire < 0) {
            throw new ExpiredOtpException("만료된 인증 번호");
        }

        return expire;
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
