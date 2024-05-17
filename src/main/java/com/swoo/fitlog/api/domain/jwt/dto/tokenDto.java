package com.swoo.fitlog.api.domain.jwt.dto;

import lombok.*;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class tokenDto {
    private String accessToken;
    private String refreshToken;
    private Duration refreshTokenDuration;
}


