package com.swoo.fitlog.http;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Set;

@Getter
@Builder
public class ErrorResponse {
    private int code;
    private HttpStatus httpStatus;
    private String message;
    private Set<Integer> errorCodes;

    public static ErrorResponse of(int code, HttpStatus httpStatus, String message, Set<Integer> errorCodes) {
        return ErrorResponse.builder()
                .code(code)
                .httpStatus(httpStatus)
                .message(message)
                .errorCodes(errorCodes)
                .build();
    }
}

