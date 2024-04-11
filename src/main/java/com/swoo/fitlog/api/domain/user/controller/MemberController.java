package com.swoo.fitlog.api.domain.user.controller;

import com.swoo.fitlog.api.domain.user.dto.MemberDto;
import com.swoo.fitlog.api.domain.user.service.MemberService;
import com.swoo.fitlog.http.RestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
