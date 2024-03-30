package com.swoo.fitlog.api.domain.auth;

import com.swoo.fitlog.api.domain.auth.dto.EmailDto;
import com.swoo.fitlog.api.domain.auth.dto.ExpireDto;
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
import org.springframework.web.bind.annotation.*;

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

    /**
     * 인증 번호의 남은 시간을 조회하는 API
     *
     * @param email 인증 번호의 남은 시간을 조회가기 위해 사용되는 key
     * @return 조회 성공<br>
     * 남은 시간을 반환한다. <br><br>
     * 조회 실패 <br>
     * 아래의 에러 코드를 반환한다.<br>
     *  에러 코드: 10(잘못된 이메일 형식)<br>
     *  에러 코드: 71(이미 만료된 인증 번호)
     * @throws com.swoo.fitlog.exception.ExpiredOtpException 이미 만료된 인증 번호라면 해당 예외가 발생한다.
     */
    @GetMapping("/api/v1/auth/locals/email/expiration")
    public ResponseEntity<Object> sendExpirationTime(@RequestParam("email") String email) {

        // Email 형식 체크
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    400,
                    HttpStatus.BAD_REQUEST,
                    "예기치 않은 오류가 발생했습니다",
                    Set.of(10)
            );

            return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
        }

        Long expiration = otpService.getExpiration(email);

        RestResponse<Object> restResponse =
                RestResponse.of(200, HttpStatus.OK, "유효 시간 획득 성공", ExpireDto.from(expiration));

        return new ResponseEntity<>(restResponse, HttpStatus.OK);
    }
}
