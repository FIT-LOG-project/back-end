package com.swoo.fitlog.exception.exceptionhandler;

import com.swoo.fitlog.exception.ExpiredOtpException;
import com.swoo.fitlog.exception.ExpiredPasswordException;
import com.swoo.fitlog.exception.NoMatchPasswordException;
import com.swoo.fitlog.http.ErrorResponse;
import com.swoo.fitlog.utils.ErrorCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /*
     * @Valid 어노테이션 예외 처리
     *
     * 1. 에러 코드를 담기 위한 Set<Integer> errorCodes 생성
     * 하나의 필드에 여러개의 @valid를 붙이면 중복으로 field를 찾게 된다.
     *
     * 2. 에러 코드 불러오기
     * 3. ErrorResponse 생성
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {

        Set<Integer> errorCodes = new HashSet<>();

        /*
         * ErrorCodeUtil 에서 필드를 찾는다.
         * ErrorCodeUtil.getFieldErrorCode(String fieldName)으로 필드에 맞는 에러 코드를 찾는다.
         * */
        exception.getFieldErrors().forEach(fieldError ->
                errorCodes.add(ErrorCodeUtil.getFieldErrorCode(fieldError.getField())));

        ErrorResponse errorResponse =
                ErrorResponse.of(400, HttpStatus.BAD_REQUEST,"예기치 않은 오류가 발생했습니다.", errorCodes);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(ExpiredOtpException.class)
    public ResponseEntity<Object> ExpiredOtpException(ExpiredOtpException exception) {
        log.error("[ERROR][ExpiredOtpException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                400,
                HttpStatus.BAD_REQUEST,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.OTP_EXPIRED_TIME.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    /**
     * 회원 가입 시 비밀 번호 재확인을 위해 저장된 임시 비밀 번호가 만료된 경우 발생한 예외를 처리한다.
     * @param exception 만료된 비밀 번호로 발생한 ExpiredPasswordException 예외
     * @return 에러코드: 21(만료된 비밀 번호)
     */
    @ExceptionHandler(ExpiredPasswordException.class)
    public ResponseEntity<Object> ExpiredPasswordException(ExpiredPasswordException exception) {
        log.error("[ERROR][ExpiredPasswordException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                400,
                HttpStatus.BAD_REQUEST,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.PASSWORD_EXPIRED_TIME.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    /**
     * 비밀번호가 틀린 경우 발생한 예외를 처리한다.
     * @param exception 틀린 비밀번호로 로그인을 요청하여 발생한 예외
     * @return 에러코드 22
     */
    @ExceptionHandler(NoMatchPasswordException.class)
    public ResponseEntity<ErrorResponse> NoMatchPasswordException(NoMatchPasswordException exception) {
        log.error("[ERROR][NoMatchPasswordException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                401,
                HttpStatus.UNAUTHORIZED,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.PASSWORD_FAIL_AUTH.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}