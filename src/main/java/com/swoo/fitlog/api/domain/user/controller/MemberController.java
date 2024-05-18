package com.swoo.fitlog.api.domain.user.controller;

import com.swoo.fitlog.api.domain.user.MemberStatus;
import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.dto.MemberInfoDto;
import com.swoo.fitlog.api.domain.user.entity.Member;
import com.swoo.fitlog.api.domain.user.service.MemberService;
import com.swoo.fitlog.http.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/v1/auth/locals")
    public ResponseEntity<RestResponse<Object>> joinMember(@RequestBody @Valid MemberDto joinMember) {

        memberService.join(joinMember);

        RestResponse<Object> restResponse =
                RestResponse.of(200, HttpStatus.OK, "회원 가입 성공", null);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("/api/v1/nickname")
    public ResponseEntity<RestResponse<Object>> editNickname(@RequestBody @Valid MemberInfoDto memberInfoDto) {
        log.info("updateMember: {}", memberInfoDto.toString());

        String email = memberInfoDto.getEmail();
        String nickname = memberInfoDto.getNickname();
        MemberStatus status = memberInfoDto.getStatus();

        memberService.updateNickname(email, nickname);

        if (status == MemberStatus.NEW) {
            memberService.updateStatus(email, MemberStatus.NORMAL);
        }

        Member findMember = memberService.findByEmail(memberInfoDto.getEmail());
        MemberInfoDto findMemberInfoDto = MemberInfoDto.builder()
                .email(findMember.getEmail())
                .nickname(findMember.getNickname())
                .status(findMember.getStatus())
                .build();

        RestResponse<Object> restResponse =
                RestResponse.of(200, HttpStatus.OK, "닉네임 설정 완료", findMemberInfoDto);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
