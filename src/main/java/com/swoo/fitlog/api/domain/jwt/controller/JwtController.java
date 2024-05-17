package com.swoo.fitlog.api.domain.jwt.controller;

import com.swoo.fitlog.api.domain.auth.dto.EmailDto;
import com.swoo.fitlog.api.domain.auth.service.LoginService;
import com.swoo.fitlog.api.domain.jwt.dto.tokenDto;
import com.swoo.fitlog.api.domain.jwt.service.TokenService;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.MemberInfoDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.service.MemberService;
import com.swoo.fitlog.http.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final TokenService tokenService;
    private final MemberService memberService;
    private final LoginService loginService;

    @PostMapping("/api/v1/auth/local")
    public ResponseEntity<RestResponse<Object>> login(@RequestBody @Valid MemberDto loginMember) {
        HttpHeaders httpHeaders = new HttpHeaders();

        Member authMember = memberService.findByEmail(loginMember.getEmail());
        MemberInfoDto authMemberInfo = loginService.authenticate(authMember, loginMember.getPassword());

        tokenDto tokenDto = tokenService.authenticateUserAndGenerateToken(loginMember);

        addAccessTokenToHeader(httpHeaders, tokenDto.getAccessToken());
        addRefreshTokenToCookie(httpHeaders, tokenDto.getRefreshToken(), tokenDto.getRefreshTokenDuration());

        return successResponseHttpStatusOK(httpHeaders, "로그인 성공", authMemberInfo);
    }

    @PostMapping("/api/v1/auth/refresh-token")
    public ResponseEntity<RestResponse<Object>> verifyRefreshToken(@CookieValue("RefreshToken") String refreshToken,
                                                                   @RequestHeader("Authorization") String accessToken,
                                                                   @RequestBody @Valid EmailDto emailAuth) {

        String email = emailAuth.getEmail();
        HttpHeaders httpHeaders = new HttpHeaders();
        accessToken = accessToken.substring("Bearer ".length());

        String reIssuedAccessToken = tokenService.reIssueAccessToken(refreshToken, accessToken, email);
        addAccessTokenToHeader(httpHeaders, reIssuedAccessToken);

        return successResponseHttpStatusOK(httpHeaders, "액세스 토큰 재발급 완료", null);
    }

    private void addAccessTokenToHeader(HttpHeaders httpHeaders, String accessToken) {
        httpHeaders.add("Authorization", accessToken);
    }

    private void addRefreshTokenToCookie(HttpHeaders httpHeaders, String refreshToken, Duration refreshTokenDuration) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .path("/api/v1/auth/refresh-token")
                .sameSite("none")
                .secure(true)
                .httpOnly(true)
                .maxAge(refreshTokenDuration)
                .build();

        httpHeaders.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
    }

    private ResponseEntity<RestResponse<Object>> successResponseHttpStatusOK(HttpHeaders httpHeaders,
                                                                             String msg,
                                                                             Object data) {
        RestResponse<Object> restResponse = RestResponse.of(200, HttpStatus.OK, msg, data);

        return new ResponseEntity<>(restResponse, httpHeaders, restResponse.getHttpStatus());
    }
}
