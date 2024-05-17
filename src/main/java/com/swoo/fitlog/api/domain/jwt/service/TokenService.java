package com.swoo.fitlog.api.domain.jwt.service;

import com.swoo.fitlog.api.domain.jwt.dto.tokenDto;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.exception.IncorrectRefreshTokenException;
import com.swoo.fitlog.exception.ReissueAccessTokenException;
import com.swoo.fitlog.exception.TokenSecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    /**
     * 사용자를 인증하고 새로운 JWT, 즉 Access Token, Refresh Token을 생성한다.
     * @param loginMember 인증하려는 사용자
     */
    public tokenDto authenticateUserAndGenerateToken(MemberDto loginMember) {
        String accessToken = accessTokenService.generateAccessToken(loginMember.getEmail());
        String refreshToken = refreshTokenService.findOrIssueRefreshToken(loginMember.getEmail());
        Duration refreshTokenDuration = extractRefreshTokenDuration(refreshToken);

        return tokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenDuration(refreshTokenDuration)
                .build();
    }

    /**
     * <p>만료된 Access Token을 재발급 한다.</p>
     * <p>
     *     Refresh Token 검증에 실패하거나, 만료되지 않은 Access Token 이라면 토큰을 삭제하고 로그 아웃한다.
     * </p>
     *
     * @param refreshToken Access Token을 재발급하기 위해 필요한 토큰
     * @param accessToken 만료되어 재발급이 필요한 Access Token
     * @param email Refresh Token을 가지고 있는 사용자의 이메일
     * @throws TokenSecurityException 만료되지 않은 Access Token 재발급, Refresh Token 검증 실패 시 해당 예외가 발생한다.
     */
    public String reIssueAccessToken(String refreshToken, String accessToken, String email) {

        try {

            accessTokenService.verifyAccessTokenForReissue(accessToken);
            refreshTokenService.verifyRefreshTokenForIssueAccessToken(email, refreshToken);

        } catch (ReissueAccessTokenException | IncorrectRefreshTokenException e) {

            /*
            * 로그 아웃 진행
            * */
            accessTokenService.logout(accessToken);
            refreshTokenService.deleteRefreshToken(email);
            throw new TokenSecurityException("토큰 탈취 가능성 존재, 로그 아웃 진행");

        }

        return accessTokenService.generateAccessToken(email);
    }

    private Duration extractRefreshTokenDuration(String refreshToken) {
        return Duration.ofMinutes(refreshTokenService.getRefreshTokenExpiredTime(refreshToken));
    }
}