package com.swoo.fitlog.api.domain.user.repository;

import com.swoo.fitlog.api.domain.user.entity.Member;

import java.util.List;

public interface MemberRepository {

    Member save(Member member);

    Member findById(Long id);

    List<Member> findAll();

    void update(Long id, Member memberUpdate);

    void deleteById(Long id);
}
