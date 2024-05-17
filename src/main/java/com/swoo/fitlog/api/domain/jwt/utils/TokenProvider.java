package com.swoo.fitlog.api.domain.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    /**
     * Access Token의 만료 시간, 기본 30분
     */
    private static final Long ACCESS_TOKEN_EXPIRED_TIME = 1000 * 60 * 30L;

    /**
     * Refresh Token의 만료 시간, 기본 일주일
     */
    private static final Long REFRESH_TOKEN_EXPIRED_TIME = 1000 * 60 * 60 * 24 * 7L;

    /**
     * Refresh Token을 재발급하기 위해 필요한 최대 남은 만료 시간, 1일
     */
    private static final Long REFRESH_TOKEN_MIN_TIME = 1000 * 60 * 60 * 24L;

    /**
     * SecretKey를 가져오기 위한 secretKey 매니저
     */
    private final SecretKeyManager secretKeyManager;

    /**
     * Access Token, Refresh Token 서명에 필요한 secret key
     */
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = secretKeyManager.getSecretKey();
    }

    /**
     * Access Token을 생성한다.
     * @param email Access Token의 Payload에 저장할 이메일
     * @return <code>String</code> 생성된 <code>Access Token</code>을 반환
     */
    public String generateAccessToken(String email) {
        return tokenBuild(email, ACCESS_TOKEN_EXPIRED_TIME);
    }

    /**
     * Refresh Token을 생성한다.
     *
     * @return <code>String</code> 생성된 <code>Refresh Token</code>을 반환
     */
    public String generateRefreshToken() {
        return tokenBuild("REFRESH", REFRESH_TOKEN_EXPIRED_TIME);
    }

    /**
     * 실제 Access Token, Refresh Token을 생성한다.
     *
     * @param subject Payload의 subject에 입력될 매개 변수
     * @param tokenExpiredTime 토큰의 만료시간을 설정하기 위한 매개 변수
     *
     * @return <code>String</code> 생성된 토큰을 반환한다.
     */
    private String tokenBuild(String subject, Long tokenExpiredTime) {
        return Jwts.builder()
                .header()
                .and()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpiredTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 올바른 Access Token 인지 검증한다.
     * @param accessToken 검증 하려는 Access Token
     *
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰의 만료 시간이 종료되었다면 해당 예외가 발생한다.
     * @throws io.jsonwebtoken.security.SignatureException secretkey로 검증에 실패했다면 해당 예외가 발생한다.
     */
    public void verifyAccessToken(String accessToken) {
        extractClaimsAndVerifyToken(accessToken);
    }

    /**
     * Refresh Token의 만료 시간이 재발급이 필요한 특정 만료 시간 이내인지 확인한다.
     * @param refreshToken 재발급이 필요한 토큰인지 검증하기 위한 Refresh Token
     * @return 재발급이 필요하면 <code>true</code>, 재발급이 필요하지 않으면 <code>false</code>
     */
    public boolean verifyRefreshTokenExpiredForReIssuance(String refreshToken) {
        Jws<Claims> claimsJws = extractClaimsAndVerifyToken(refreshToken);
        Date expiration = claimsJws.getPayload().getExpiration();
        long currentTimeLeft = expiration.getTime() - System.currentTimeMillis();
        return REFRESH_TOKEN_MIN_TIME >= currentTimeLeft;
    }

    /**
     * 토큰의 만료 시간을 조회한다.
     * @param token 만료 시간을 조회 할 토큰
     * @return <code>Long</code> 조회한 만료 시간 반환
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰의 만료 시간이 종료되었다면 해당 예외가 발생한다.
     * @throws io.jsonwebtoken.security.SignatureException secretkey로 검증에 실패했다면 해당 예외가 발생한다.
     */
    public Long extractTokenExpiredTime(String token) {
        Jws<Claims> claimsJws = extractClaimsAndVerifyToken(token);
        return claimsJws.getPayload().getExpiration().getTime();
    }

    /**
     * 토큰을 검증하고 이메일을 반환한다.
     * @param token 이메일을 찾기 위한 토큰
     * @return <code>String</code> 토큰의 Payload에서 찾은 이메일 반환
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰의 만료 시간이 종료되었다면 해당 예외가 발생한다.
     * @throws io.jsonwebtoken.security.SignatureException secretkey로 검증에 실패했다면 해당 예외가 발생한다.
     */
    public String extractEmailAndVerifyToken(String token) {
        Jws<Claims> claimsJws = extractClaimsAndVerifyToken(token);
        return claimsJws.getPayload().getSubject();
    }

    /**
     * 토큰을 검증하고 Claims를 반환한다.
     * @param token Claims를 찾기 위한 토큰
     * @return Claims 반환
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰의 만료 시간이 종료되었다면 해당 예외가 발생한다.
     * @throws io.jsonwebtoken.security.SignatureException secretkey로 검증에 실패했다면 해당 예외가 발생한다.
     */
    private Jws<Claims> extractClaimsAndVerifyToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }
}