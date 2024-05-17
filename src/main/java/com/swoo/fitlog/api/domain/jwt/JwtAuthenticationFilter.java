package com.swoo.fitlog.api.domain.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoo.fitlog.api.domain.jwt.service.AccessTokenService;
import com.swoo.fitlog.exception.LogoutTokenException;
import com.swoo.fitlog.http.ErrorResponse;
import com.swoo.fitlog.utils.ErrorCodeUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        switch (requestURI) {
            case "/api/v1/auth/locals",
                 "/api/v1/auth/local",
                 "/api/v1/auth/refresh-token" -> {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            createResponse(response, getErrorResponse());
            return;
        }

        String accessToken = authorization.substring("Bearer ".length());
        verifyToken(accessToken, request, response, filterChain);
    }

    private void createResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        String errorResponseJSON = objectMapper.writeValueAsString(errorResponse);
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(errorResponse.getCode());
        response.getWriter().write(errorResponseJSON);
    }

    public void verifyToken(String token,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain filterChain) throws IOException {
        request.getCookies();

        try {
            accessTokenService.verifyAccessToken(token);
            log.info("[토큰 인증 성공]");
            filterChain.doFilter(request, response);
        } catch (LogoutTokenException | ExpiredJwtException | SignatureException e) {
            log.error("[토큰 인증 실패]");
            createResponse(response, getErrorResponse());
        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }

    private static ErrorResponse getErrorResponse() {
        return ErrorResponse.of(401,
                HttpStatus.UNAUTHORIZED,
                "토큰 인증에 실패하였습니다.",
                Set.of(ErrorCodeUtil.INCORRECT_TOKEN.getErrorCode())
        );
    }

}
