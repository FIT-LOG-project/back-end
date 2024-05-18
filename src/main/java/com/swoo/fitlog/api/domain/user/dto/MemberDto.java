package com.swoo.fitlog.api.domain.user.dto;

import com.swoo.fitlog.api.domain.user.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Builder
@Getter @Setter
public class MemberDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 6, max = 15)
    private String password;

    public Member toEntity() {
        return Member.builder()
                .email(this.email)
                .password(this.password)
                .build();
    }
}
