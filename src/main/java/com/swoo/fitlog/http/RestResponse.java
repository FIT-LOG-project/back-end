package com.swoo.fitlog.http;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class RestResponse<T>{
    private int code;
    private HttpStatus httpStatus;
    private String message;
    private T data;

    public static <T> RestResponse<T> of(int code, HttpStatus httpStatus, String message, T data) {
        return RestResponse.<T>builder()
                .code(code)
                .httpStatus(httpStatus)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> RestResponse<T> ok(String message, T data) {
        return RestResponse.<T>builder()
                .code(200)
                .httpStatus(HttpStatus.OK)
                .message(message)
                .data(data)
                .build();
    }
}

