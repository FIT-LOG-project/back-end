package com.swoo.fitlog.api.domain.user.entity;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Member {

    private Long id;
    private String email;
    private String password;
    private MemberStatus status;
}
