package com.swoo.fitlog.api.domain.user.dto;

import com.swoo.fitlog.api.domain.user.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordVerifyDto {

    private String password;
    private String reconfirmPassword;

    public Member toEntity() {
        return Member.builder()
                .password(this.password)
                .build();
    }
}
