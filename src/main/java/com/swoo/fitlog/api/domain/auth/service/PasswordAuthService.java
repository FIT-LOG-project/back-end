package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.exception.ExpiredPasswordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordAuthService {

    private static final Long EXPIRATION_TIME_MINUTES = 5L; // 유효 시간 5분

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원 가입을 위해 입력한 비밀 번호를 임시 저장한다.
     * @param email 임시로 비밀 번호를 저장하기 위한 Key
     * @param password 회원 가입을 위해 입력한 비밀 번호
     */
    public void saveTemporaryPassword(String email, String password) {
        String key = generateKey(email);

        redisTemplate.opsForValue().set(key, password, EXPIRATION_TIME_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 회원 가입을 위해 입력한 비밀 번호와 재확인 비밀 번호를 인증한다.
     * @param email 임시 비밀 번호를 찾기 위한 key
     * @param reconfirmPassword 재확인 비밀 번호
     * @return 인증 성공 - true<br>
     * 인증 실패 - false
     * @throws ExpiredPasswordException 존재하지 않는 key인 경우, 만료된 비밀 번호로 간주하고 해당 예외를 던진다.
     */
    public boolean certifyPassword(String email, String reconfirmPassword) {
        String key = generateKey(email);

        String savedPassword = redisTemplate.opsForValue().get(key);

        if (savedPassword == null) {
            throw new ExpiredPasswordException("만료된 비밀 번호");
        }

        /* 재확인 비밀 번호가 일치하면 redis 저장소에서 삭제후 true 반환 */
        if (savedPassword.equals(reconfirmPassword)) {
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    private String generateKey(String email) {
        return "PWD:" + email;
    }

}
