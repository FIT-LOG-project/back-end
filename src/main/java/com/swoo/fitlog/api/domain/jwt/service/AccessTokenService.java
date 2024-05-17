package com.swoo.fitlog.api.domain.jwt.service;

import com.swoo.fitlog.api.domain.jwt.repository.AccessTokenRepository;
import com.swoo.fitlog.api.domain.jwt.utils.TokenProvider;
import com.swoo.fitlog.exception.LogoutTokenException;
import com.swoo.fitlog.exception.ReissueAccessTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final AccessTokenRepository accessTokenRepository;
    private final TokenProvider tokenProvider;

    public String generateAccessToken(String accessToken) {
        return tokenProvider.generateAccessToken(accessToken);
    }

    /**
     * Access Token을 로그아웃 처리한다.
     * @param accessToken 로그아웃 처리를 하기위한 Access Token
     */
    public void logout(String accessToken) {
        Long accessTokenExpiredTime = tokenProvider.extractTokenExpiredTime(accessToken);
        accessTokenRepository.save(accessToken, accessTokenExpiredTime);
    }

    /**
     * 유효한 Access Token 인지 검증한다.
     * @param accessToken 검증이 필요한 Access Token
     */
    public void verifyAccessToken(String accessToken) {
        isLogoutAccessToken(accessToken);
        tokenProvider.verifyAccessToken(accessToken);
    }

    /**
     * Access Token Payload 내부에 저장된 email을 찾는다.
     * @param accessToken - 이메일을 얻기 위한 Access Token
     * @return <code>String</code> Access Token Payload에서 찾은 이메일
     */
    public String getEmail(String accessToken) {
        return tokenProvider.extractEmailAndVerifyToken(accessToken);
    }

    /**
     * <p>Access Token 재발급을 위해 현재 Access Token을 검증한다.</p>
     * <p>
     *     Access Token 재발급은 만료된 Access Token만 가능하다.<br>
     *     따라서 만료되지 않은 Access Token이 재발급을 요청.<br>
     *     secret key를 사용한 검증이 실패했다면 Token이 탈취 된 것으로 간주한다.
     * </p>
     * @param accessToken - 재발급이 필요한지 검증하기 위한 Access Token
     * @throws ReissueAccessTokenException 만료되지 않은 Access Token이 매개 변수로 입력 받은 경우 해당 예외가 발생한다.
     */
    public void verifyAccessTokenForReissue(String accessToken) {
        try {
            tokenProvider.verifyAccessToken(accessToken);
        } catch (ExpiredJwtException e) {
            log.info("[INFO][Access Token 만료 확인][재발급 진행]");
        } catch (SignatureException e) {
            throw new ReissueAccessTokenException("만료되지 않은 Access Token 재발급 요청");
        }
    }

    /**
     * Access Token이 로그아웃 처리된 토큰인지 확인한다.
     * @param accessToken 로그아웃 처리된 토큰인지 확인하기 위한 Access Token
     * @throws LogoutTokenException 로그 아웃 처리된 토큰이 매개 변수로 들어오면 해당 예외가 발생한다.
     */
    private void isLogoutAccessToken(String accessToken) {
        String findAccessToken = accessTokenRepository.findAccessToken(accessToken);

        if (findAccessToken != null) {
            throw new LogoutTokenException("로그아웃한 토큰입니다.");
        }
    }
}
