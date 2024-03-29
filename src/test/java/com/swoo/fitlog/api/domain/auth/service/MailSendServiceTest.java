package com.swoo.fitlog.api.domain.auth.service;

import com.swoo.fitlog.api.domain.auth.service.MailSendService;
import com.swoo.fitlog.api.domain.auth.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailSendServiceTest {


    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailSendService mailSendService;

    @Mock
    private OtpService otpService;

    @Test
    @DisplayName("이메일로 인증 번호 발송")
    void sendOtpToMail() throws MessagingException {
        // Arrange
        String targetEmail = "test@example.com";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailSendService.send(targetEmail);

        verify(mailSender).send(any(MimeMessage.class)); // 메일이 전송되었는지 확인
    }
}
