package com.swoo.fitlog.api.domain.user.service;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.PasswordVerifyDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void join(MemberDto joinMember) {
        Member member = joinMember.toEntity();
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        memberRepository.save(member);
    }

    @Override
    public void updatePassword(Long id, PasswordVerifyDto updateMember) {
        Member member = updateMember.toEntity();

        String password = member.getPassword();
        member.setPassword(passwordEncoder.encode(password));

        member.setStatus(MemberStatus.NEW);

        memberRepository.updatePassword(id, member);
    }

    @Override
    public void updateNickname(String email, String nickname) {
        memberRepository.updateNickname(email, nickname);
    }

    @Override
    public void updateStatus(String email, MemberStatus status) {
        memberRepository.updateStatus(email, status);
    }

    @Override
    public Member findById(Long id) {
        return memberRepository.findById(id);
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}
