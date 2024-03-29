package com.swoo.fitlog.api.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSendService {

    private final JavaMailSender mailSender;
    private final OtpService otpService;

    /*
     * 인증 번호 전송
     * */
    public void send(String targetEmail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(targetEmail);
        helper.setSubject("[FIT-LOG] 회원 가입 인증 번호 전송");

        /*
         * 인증 번호 생성
         * */
        int otp = otpService.generateAndSaveOTP(targetEmail);
        helper.setText("인증 번호: " + otp);

        mailSender.send(message);
    }
}
