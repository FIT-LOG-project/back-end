package com.swoo.fitlog.api.domain.user.repository;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.entity.Member;

import java.util.List;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long id);

    Member findByEmail(String email);

    List<Member> findAll();

    void updatePassword(Long id, Member memberUpdate);

    void updateNickname(String email, String nickname);

    void updateStatus(String email, MemberStatus memberStatus);

    void deleteById(Long id);
}
