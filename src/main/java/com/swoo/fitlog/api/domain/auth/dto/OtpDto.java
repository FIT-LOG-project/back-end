package com.swoo.fitlog.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class OtpDto {

    @Pattern(regexp = "(\\d{6}|\"\\d{6}\")")
    private String otp;

    @NotBlank
    @Email
    private String email;

    public int getOtp() {
        return Integer.parseInt(otp);
    }
}
