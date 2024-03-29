package com.swoo.fitlog.api.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmailDto {
    @NotBlank
    @Email
    private String email;
}
