package com.swoo.fitlog.api.domain.auth;

import com.swoo.fitlog.api.domain.auth.dto.EmailDto;
import com.swoo.fitlog.api.domain.auth.dto.OtpDto;
import com.swoo.fitlog.api.domain.auth.service.MailSendService;
import com.swoo.fitlog.api.domain.auth.service.OtpService;
import com.swoo.fitlog.http.ErrorResponse;
import com.swoo.fitlog.http.RestResponse;
import com.swoo.fitlog.utils.ErrorCodeUtil;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final MailSendService mailSendService;
    private final OtpService otpService;

    /*
        이메일 수신 후 인증 번호 발송
     */
    @PostMapping("/api/v1/auth/locals/email")
    public ResponseEntity<Object> receiveEmail(@Valid @RequestBody EmailDto emailDto) throws MessagingException {

        String email = emailDto.getEmail();
        log.debug("[E-mail]=[{}]", email);

        mailSendService.send(email);

        RestResponse<Object> restResponse = RestResponse.builder()
                .code(200)
                .httpStatus(HttpStatus.OK)
                .message("이메일 인증 번호 발송")
                .build();

        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }

    /**
     * 인증 번호의 유효성을 검사하는 API
     *
     * @param otpDto 클라이언트에게 받은 이메일과 인증 번호
     * @return
     * 성공 <br>
     * code:200 <br>
     * httpStatus: OK <br>
     * message: 이메일 인증 성공 <br>
     * data: null <br><br>
     * 실패
     * code: 400 <br>
     * httpStatus: BAD_REQUEST <br>
     * message: 예기치 않은 오류가 발생했습니다. <br>
     * errorCodes: 70, 71, 72, 10 <br>
     */
    @PostMapping("/api/v1/auth/locals/email/otp")
    public ResponseEntity<Object> certifyOtp(@RequestBody @Valid OtpDto otpDto) {
        int otp = otpDto.getOtp();
        String email = otpDto.getEmail();

        /*
            일치 하지 않는 인증 번호일 때, OTP_NOT_AGREEMENT 에러 코드를 ErrorResponse에 담아 클라이언트에 보내준다.
         */
        if (!otpService.verifyOTP(email, otp)) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    400,
                    HttpStatus.BAD_REQUEST,
                    "예기치 않은 오류가 발생했습니다.",
                    Set.of(ErrorCodeUtil.OTP_NOT_AGREEMENT.getErrorCode())
            );

            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        // 인증 성공
        RestResponse<Object> restResponse = RestResponse
                .of(200, HttpStatus.OK, "이메일 인증 성공", null);

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }
}
