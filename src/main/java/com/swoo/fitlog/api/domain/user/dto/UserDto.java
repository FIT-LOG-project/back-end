package com.swoo.fitlog.api.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
public class UserDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 15)
    private String password;
}
