package com.swoo.fitlog.api.domain.jwt.service;

import com.swoo.fitlog.api.domain.jwt.JwtTokenProvider;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.exception.IncorrectRefreshTokenException;
import com.swoo.fitlog.exception.ReissueAccessTokenException;
import com.swoo.fitlog.exception.TokenSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtTokenProvider jwtTokenProvider;
    private final DaoAuthenticationProvider authenticationProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;

    /**
     * 사용자를 인증하고 새로운 JWT, 즉 Access Token, Refresh Token을 생성한다.
     * @param loginMember 인증하려는 사용자
     * @param httpHeaders response header에 토큰 정보를 담기 위해 사용하는 매개 변수
     */
    public void authenticateUserAndGenerateJwt(MemberDto loginMember, HttpHeaders httpHeaders) {
        authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(loginMember.getEmail(), loginMember.getPassword()));

        addJwtToHeader(httpHeaders, loginMember.getEmail());
        addRefreshTokenToCookie(httpHeaders, loginMember.getEmail());
    }

    /**
     * <p>만료된 Access Token을 재발급 한다.</p>
     * <p>
     *     Refresh Token 검증에 실패하거나, 만료되지 않은 Access Token 이라면 토큰을 삭제하고 로그 아웃한다.
     * </p>
     *
     * @param httpHeaders 재발급한 Access Token, Refresh Token을 헤더에 담기 위한 매개 변수
     * @param refreshToken Access Token을 재발급하기 위해 필요한 토큰
     * @param accessToken 만료되어 재발급이 필요한 Access Token
     * @param email Refresh Token을 가지고 있는 사용자의 이메일
     * @throws TokenSecurityException 만료되지 않은 Access Token 재발급, Refresh Token 검증 실패 시 해당 예외가 발생한다.
     */
    public void reIssueAccessToken(HttpHeaders httpHeaders,
                                   String refreshToken,
                                   String accessToken,
                                   String email) {

        try {
            accessTokenService.verifyAccessTokenForReissue(accessToken);
            refreshTokenService.verifyRefreshTokenForIssueAccessToken(email, refreshToken);
        } catch (ReissueAccessTokenException | IncorrectRefreshTokenException e) {
            accessTokenService.logout(accessToken);
            refreshTokenService.deleteRefreshToken(email);
            throw new TokenSecurityException("토큰 탈취 가능성 존재, 로그 아웃 진행");
        }

        addJwtToHeader(httpHeaders, email);
    }

    private void addJwtToHeader(HttpHeaders httpHeaders, String email) {
        String jwt = jwtTokenProvider.generateAccessToken(email);
        httpHeaders.add("Authorization", jwt);
    }

    private void addRefreshTokenToCookie(HttpHeaders httpHeaders, String email) {
        String refreshToken = refreshTokenService.findOrIssueRefreshToken(email);

        Long refreshTokenExpiredTime = jwtTokenProvider.getTokenExpiredTime(refreshToken);
        Duration duration = Duration.ofMinutes(refreshTokenExpiredTime);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .path("/api/v1/auth/refresh-token")
                .sameSite("none")
                .secure(true)
                .httpOnly(true)
                .maxAge(duration)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }
}