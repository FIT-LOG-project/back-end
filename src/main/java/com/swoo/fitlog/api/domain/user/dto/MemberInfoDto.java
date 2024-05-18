package com.swoo.fitlog.api.domain.user.dto;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Builder
@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Length(min = 2, max = 10)
    private String nickname;

    @NotNull
    private MemberStatus status;
}
