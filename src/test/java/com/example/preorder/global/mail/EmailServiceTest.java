package com.example.preorder.global.mail;

import com.example.preorder.common.exception.InternalErrorException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ActiveProfiles("test")
@SpringBootTest
class EmailServiceTest {
    @Mock
    JavaMailSender mailSender;

    @Spy
    @InjectMocks
    EmailService emailService;

    @Test
    @DisplayName("메일을 보낼 때, 수신자, 타이틀, 코드 값 중 어떤 값이어도 null이면 안된다.")
    void receiver_and_title_and_key_must_be_not_null() {
        String receiver = createRandomString();
        String title = createRandomString();
        String code = createRandomString();

        // Case 1: when receiver is null
        assertThatThrownBy(() -> emailService.sendEmail(null, title, code))
                .isInstanceOf(NullPointerException.class);
        // Case 2: when title is null
        assertThatThrownBy(() -> emailService.sendEmail(receiver, null, code))
                .isInstanceOf(NullPointerException.class);
        // Case 3: when code is null
        assertThatThrownBy(() -> emailService.sendEmail(receiver, title, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("메일을 보낼 때, 예외 발생하면 오류를 반환해야 한다.")
    void invalid_send_email_when_sender_throws_exception() {
        // given
        String receiver = createRandomString();
        String title = createRandomString();
        String code = createRandomString();

        // when
        doThrow(new RuntimeException()).when(mailSender).send(any(MimeMessage.class));

        // then
        assertThatThrownBy(() -> emailService.sendEmail(receiver, title, code))
                .isInstanceOf(InternalErrorException.class);
    }

    @Test
    @DisplayName("정상적으로 메일을 보내면, 예외가 발생하지 않고, send() 메소드가 1번 호출된다.")
    void valid_send_email_then_call_method() {
        // given
        String receiver = createRandomString();
        String title = createRandomString();
        String code = createRandomString();

        // when
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        emailService.sendEmail(receiver, title, code);

        // then
        verify(emailService, times(1))
                .sendEmail(anyString(), anyString(), anyString());
    }

    private String createRandomString() {
        return UUID.randomUUID().toString();
    }
}