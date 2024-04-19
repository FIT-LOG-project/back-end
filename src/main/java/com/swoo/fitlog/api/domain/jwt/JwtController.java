package com.swoo.fitlog.api.domain.jwt;

import com.swoo.fitlog.api.domain.auth.dto.EmailDto;
import com.swoo.fitlog.api.domain.jwt.service.JwtService;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.http.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/api/v1/auth/local")
    public ResponseEntity<Object> login(@RequestBody @Valid MemberDto loginMember) {

        HttpHeaders headers = new HttpHeaders();

        jwtService.authenticateUserAndGenerateJwt(loginMember, headers);

        RestResponse<Object> restResponse = RestResponse.of(200, HttpStatus.OK, "로그인 성공", null);
        return new ResponseEntity<>(restResponse, headers, restResponse.getHttpStatus());
    }

    @PostMapping("/api/v1/auth/refresh-token")
    public ResponseEntity<RestResponse<Object>> verifyRefreshToken(@CookieValue("RefreshToken") String refreshToken,
                                                                   @RequestHeader("Authorization") String accessToken,
                                                                   @RequestBody @Valid EmailDto emailAuth) {

        String email = emailAuth.getEmail();
        HttpHeaders httpHeaders = new HttpHeaders();
        accessToken = accessToken.substring("Bearer ".length());
        RestResponse<Object> restResponse;

        jwtService.reIssueAccessToken(httpHeaders, refreshToken, accessToken, email);
        restResponse = RestResponse.of(200, HttpStatus.OK, "액세스 토큰 재발급 완료", null);

        return new ResponseEntity<>(restResponse, httpHeaders, restResponse.getHttpStatus());
    }
}
