package com.swoo.fitlog.exception;

import com.swoo.fitlog.http.ErrorResponse;
import com.swoo.fitlog.utils.ErrorCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice
public class DataExceptionHandler {

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> duplicatedEmailException(DuplicatedEmailException exception) {
        log.error("[ERROR][DuplicatedEmailException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                409,
                HttpStatus.CONFLICT,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.EMAIL_DUPLICATE.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(NotExistMemberException.class)
    public ResponseEntity<ErrorResponse> noExistMemberException(NotExistMemberException exception) {
        log.error("[ERROR][NoExistMemberException][{}]", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                204,
                HttpStatus.NO_CONTENT,
                "예기치 않은 오류가 발생했습니다.",
                Set.of(ErrorCodeUtil.NOT_EXIST_MEMBER.getErrorCode())
        );

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
