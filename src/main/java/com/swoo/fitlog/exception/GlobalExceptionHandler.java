package com.swoo.fitlog.exception;

import com.swoo.fitlog.utils.ErrorCodeUtil;
import com.swoo.fitlog.http.ErrorResponse;
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
}
