package com.swoo.fitlog.api.domain.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String email, String refreshToken, long expireTime) {
        redisTemplate.opsForValue().set(
                email,
                refreshToken,
                expireTime - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    public String findRefreshToken(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void deleteRefreshToken(String email) {
        redisTemplate.delete(email);
    }
}
