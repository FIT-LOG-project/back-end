package com.swoo.fitlog.api.domain.user.repository;

import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.exception.DuplicatedEmailException;
import com.swoo.fitlog.exception.NotExistMemberException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
class JdbcMemberRepositoryTest {

    @Autowired
    MemberRepository jdbcMemberRepository;

    @Test
    @DisplayName("회원 가입 성공")
    void saveMember() {
        // given
        String email = "test@example.com";
        String password = "123456";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        // when
        Member saveMember = jdbcMemberRepository.save(member);

        // then
        assertThat(member).isEqualTo(saveMember);
    }

    @Test
    @DisplayName("중복된 이메일로 회원 가입 시도")
    void duplicateEmailInJoin() {
        // given
        String email = "test@example.com";
        String password = "123456";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        jdbcMemberRepository.save(member);

        // when
        String newEmail = "test@example.com";
        String newPassword = "123456";

        Member newMember = Member.builder()
                .email(newEmail)
                .password(newPassword)
                .build();

        // then
        assertThrows(DuplicatedEmailException.class, () -> jdbcMemberRepository.save(newMember));
    }

    @Test
    @DisplayName("회원 찾기 성공")
    void findById() {
        // given
        String email = "test@example.com";
        String password = "123456";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member saveMember = jdbcMemberRepository.save(member);

        // when
        Member findMember = jdbcMemberRepository.findById(saveMember.getId());

        // then
        assertThat(saveMember.getId()).isEqualTo(findMember.getId());
    }

    @Test
    @DisplayName("존재하지 않는 회원 조회")
    void NotExistMember() {
        // given
        Long memberId = 1L;

        // when
        // then
        assertThrows(NotExistMemberException.class, () -> jdbcMemberRepository.findById(memberId));
    }

    @Test
    @DisplayName("전체 회원 조회 성공")
    void findAll() {
        // given
        jdbcMemberRepository.save(Member.builder().email("test@example.com").password("123456").build());
        jdbcMemberRepository.save(Member.builder().email("test1@example.com").password("612345").build());
        jdbcMemberRepository.save(Member.builder().email("test2@example.com").password("134526").build());

        // when
        List<Member> members = jdbcMemberRepository.findAll();

        // then
        assertThat(3).isEqualTo(members.size());
    }

    @Test
    @DisplayName("비밀 번호 변경 성공")
    void update() {
        // given
        String email = "test@example.com";
        String password = "123456";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member saveMember = jdbcMemberRepository.save(member);

        // when
        Member updateMember = Member.builder()
                .email(email)
                .password("654321")
                .build();

        jdbcMemberRepository.update(saveMember.getId(), updateMember);

        // then
        Member findMember = jdbcMemberRepository.findById(saveMember.getId());
        assertThat(findMember.getPassword()).isEqualTo(updateMember.getPassword());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    void delete() {
        // given
        String email = "test@example.com";
        String password = "123456";

        Member member = Member.builder()
                .email(email)
                .password(password)
                .build();

        Member saveMember = jdbcMemberRepository.save(member);

        // when
        // then
        assertThat(jdbcMemberRepository.findById(saveMember.getId()).getId()).isEqualTo(1);

        jdbcMemberRepository.deleteById(saveMember.getId());
        assertThrows(NotExistMemberException.class, () -> jdbcMemberRepository.findById(saveMember.getId()));
    }
}