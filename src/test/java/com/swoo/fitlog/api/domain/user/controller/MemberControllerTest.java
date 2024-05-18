package com.swoo.fitlog.api.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swoo.fitlog.api.domain.jwt.dto.tokenDto;
import com.swoo.fitlog.api.domain.jwt.service.TokenService;
import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.MemberInfoDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeEach
    void beforeEach() {
        MemberDto joinMember = MemberDto.builder()
                .email("test@test.com")
                .password("123456")
                .build();

        memberService.join(joinMember);

        tokenDto tokenDto = tokenService.authenticateUserAndGenerateToken(joinMember);
        accessToken = tokenDto.getAccessToken();
    }

    @Test
    @DisplayName("닉네임 등록, 수정 요청")
    void editNickname() throws Exception {
        // given
        String email = "test@test.com";
        String nickname = "test";
        String URI = "/api/v1/nickname";

        String memberInfoDtoJSON = objectMapper.writeValueAsString(MemberInfoDto.builder()
                .email(email)
                .nickname(nickname)
                .status(MemberStatus.NEW)
                .build());

        // when
        mvc.perform(MockMvcRequestBuilders
                        .post(URI)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberInfoDtoJSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        // then
        Member findByEmail = memberService.findByEmail(email);
        String findNickname = findByEmail.getNickname();

        assertThat(findNickname).isEqualTo(nickname);
    }

    @Test
    @DisplayName("닉네임 필드 에러")
    void NicknameFieldError() throws Exception {
        // given
        String email = "test@test.com";
        String nickname = "test";
        String URI = "/api/v1/nickname";

        String memberInfoDtoJSON = objectMapper.writeValueAsString(MemberInfoDto.builder()
                .email(email)
                .nickname(nickname)
                .status(MemberStatus.NEW)
                .build());

        // when
        // then
        mvc.perform(MockMvcRequestBuilders
                        .post(URI)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberInfoDtoJSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 에러")
    void NicknameDuplicateError() throws Exception {
        // given
        MemberDto joinMember = MemberDto.builder()
                .email("test1@test.com")
                .password("123456")
                .build();

        memberService.join(joinMember);
        memberService.updateNickname("test1@test.com", "test");

        String email = "test@test.com";
        String nickname = "test";
        String URI = "/api/v1/nickname";

        String memberInfoDtoJSON = objectMapper.writeValueAsString(MemberInfoDto.builder()
                .email(email)
                .nickname(nickname)
                .status(MemberStatus.NEW)
                .build());

        // when
        // then
        mvc.perform(MockMvcRequestBuilders
                        .post(URI)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(memberInfoDtoJSON))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andDo(print());
    }
}