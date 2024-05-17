package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.api.domain.user.dto.MemberInfoDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.exception.NoMatchPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final PasswordEncoder passwordEncoder;

    /**
     * 로그인 요청으로 들어온 비밀번호가 올바른지 확인한다.
     * @param authMember 로그인 하려는 사용자
     * @param password 사용자가 로그인을 위해 입력한 비밀 번호
     * @return MemberInfoDto 로그인 인증에 성공한 사용자의 정보를 비밀 번호를 제외하고 반환한다.
     * @throws NoMatchPasswordException 비밀번호가 일치하지 않을 때 해당 예외를 던진다.
     */
    public MemberInfoDto authenticate(Member authMember, String password) {
        if (passwordEncoder.matches(password, authMember.getPassword())) {
            return MemberInfoDto.builder()
                    .email(authMember.getEmail())
                    .nickname(authMember.getNickname())
                    .status(authMember.getStatus())
                    .build();
        }

        throw new NoMatchPasswordException("일치하지 않는 비밀 번호");
    }
}
