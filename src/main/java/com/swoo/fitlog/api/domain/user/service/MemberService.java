package com.swoo.fitlog.api.domain.user.service;

import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.PasswordVerifyDto;
import com.swoo.fitlog.api.domain.user.entity.Member;

import java.util.List;

public interface MemberService {

    void join(MemberDto joinMember);

    void update(Long id, PasswordVerifyDto updateMember);

    Member findById(Long id);

    List<Member> findAll();

    void delete(Long id);

}
