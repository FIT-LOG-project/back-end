package com.swoo.fitlog.api.domain.user.service;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.PasswordVerifyDto;
import com.swoo.fitlog.api.domain.user.entity.Member;

import java.util.List;

public interface MemberService {

    void join(MemberDto joinMember);

    void updatePassword(Long id, PasswordVerifyDto updateMember);

    void updateNickname(String email, String nickname);

    void updateStatus(String email, MemberStatus status);

    Member findById(Long id);

    Member findByEmail(String email);

    List<Member> findAll();

    void delete(Long id);

}
