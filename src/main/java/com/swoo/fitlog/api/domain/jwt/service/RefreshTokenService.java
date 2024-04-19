package com.swoo.fitlog.api.domain.jwt.service;

import com.swoo.fitlog.api.domain.jwt.JwtTokenProvider;
import com.swoo.fitlog.api.domain.jwt.repository.RefreshTokenRepository;
import com.swoo.fitlog.exception.IncorrectRefreshTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * Refresh Token을 조회하고, 이메일에 맞는 Refresh Token이 존재하지 않으면 신규 발급
     * 또는 조회한 Refresh Token의 만료 기간이 특정 기간 이내이면 재발급을 한다.
     * @param email Refresh Token을 조회하기 위해 필요한 사용자 이메일
     * @return <code>String</code> 기존의 Refresh Token 또는 새로 발급한 Refresh Token
     */
    public String findOrIssueRefreshToken(String email) {
        String refreshToken = refreshTokenRepository.findRefreshToken(email);

        /*
        * refresh token이 존재하지 않는 경우
        * 새로운 refresh token을 발급하고 저장한다.
        * */
        if (refreshToken == null) {
            refreshToken = jwtTokenProvider.generateRefreshToken();
            refreshTokenRepository.save(email, refreshToken, jwtTokenProvider.getTokenExpiredTime(refreshToken));
            return refreshToken;
        }

        /* refresh token 유효기간이 얼마 남지 않은 경우 기존의 refresh token을 삭제 후 재발급을 진행한다. */
        if (jwtTokenProvider.verifyRefreshTokenExpiredForReIssuance(refreshToken)) {
            refreshTokenRepository.deleteRefreshToken(email);

            refreshToken = jwtTokenProvider.generateRefreshToken();
            refreshTokenRepository.save(email, refreshToken, jwtTokenProvider.getTokenExpiredTime(refreshToken));
        }

        return refreshToken;
    }

    /**
     * Access Token을 재발급 받기 위해 저장된 Refresh Token과 요청으로 들어온 Refresh Token이 일치하는지 검증한다.
     *
     * @param email 저장된 Refresh Token을 찾기위해 키값으로 사용하는 이매일
     * @param refreshToken - Access Token 재발급을 위해 요청한 refresh token
     *
     * @throws IncorrectRefreshTokenException
     * 이메일로 Refresh Token을 조회할 수 없거나, 요청으로 들어온 Refresh Token과 이메일로 조회한 Refresh Token이 일치하지 않는 경우
     * 해당 예외가 발생한다.
     */
    public void verifyRefreshTokenForIssueAccessToken(String email, String refreshToken) {
        String findRefreshToken = refreshTokenRepository.findRefreshToken(email);

        /* 아래의 예외가 발생하지 않으면 Refresh Token의 검증은 성공이다. */
        if (refreshToken == null || !refreshToken.equals(findRefreshToken)) {
            throw new IncorrectRefreshTokenException("일치하지 않는 Refresh Token");
        }
    }

    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteRefreshToken(email);
    }
}
