package com.swoo.fitlog.exception.exceptionhandler;

import com.swoo.fitlog.exception.LogoutTokenException;
import com.swoo.fitlog.exception.ReissueAccessTokenException;
import com.swoo.fitlog.exception.TokenSecurityException;
import com.swoo.fitlog.http.ErrorResponse;
import com.swoo.fitlog.utils.ErrorCodeUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class JwtExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> AuthenticationException(AuthenticationException exception) {
        log.error("[ERROR][AuthenticationException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                400,
                HttpStatus.BAD_REQUEST,
                "예기치 않은 오류가 발생했습니다."
                , Set.of(ErrorCodeUtil.FAILED_LOGIN.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> ExpiredJwtException(ExpiredJwtException exception) {
        log.error("[ERROR][ExpiredJwtException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                401,
                HttpStatus.UNAUTHORIZED,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.ACCESS_TOKEN_EXPIRED.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> SignatureException(SignatureException exception) {
        log.error("[ERROR][SignatureException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                401,
                HttpStatus.UNAUTHORIZED,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.INCORRECT_ACCESS_TOKEN.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(LogoutTokenException.class)
    public ResponseEntity<ErrorResponse> LogoutTokenException(LogoutTokenException exception) {
        log.error("[ERROR][LogoutTokenException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                400,
                HttpStatus.BAD_REQUEST,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.ACCESS_TOKEN_ALREADY_LOGOUT.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(TokenSecurityException.class)
    public ResponseEntity<ErrorResponse> TokenSecurityException(TokenSecurityException exception) {
        log.error("[ERROR][TokenSecurityException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                401,
                HttpStatus.UNAUTHORIZED,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.REQUEST_LOGIN.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
