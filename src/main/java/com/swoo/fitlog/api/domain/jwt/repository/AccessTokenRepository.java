package com.swoo.fitlog.api.domain.jwt.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AccessTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * <p>
     *     Access Token을 저장소에 토큰의 만료 시간 동안 저장한다.
     * </p>
     * <p>
     *     로그 아웃 후 Access Token을 만료시간 동안 사용하지 못하게 하기 위해 일시적으로 저장한다.
     * </p>
     * @param accessToken key의 역할을 하는 Access Token
     * @param expireTime 매개 변수로 들어오는 Access Token이 만료되는 시간
     */
    public void save(String accessToken, Long expireTime) {
        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                expireTime - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * <p>
     *     저장소에서 Access Token을 찾는다.
     * </p>
     * @param accessToken Key의 역할을 하는 Access Token
     * @return 존재하지 않으면 <code>null</code>, 존재하면 <code>logout</code>
     */
    public String findAccessToken(String accessToken) {
        return redisTemplate.opsForValue().get(accessToken);
    }
}
