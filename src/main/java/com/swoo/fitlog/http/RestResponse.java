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

    public static RestResponse<Object> of(int code, HttpStatus httpStatus, String message, Object data) {
        return RestResponse.builder()
                .code(code)
                .httpStatus(httpStatus)
                .message(message)
                .data(data)
                .build();
    }
}

