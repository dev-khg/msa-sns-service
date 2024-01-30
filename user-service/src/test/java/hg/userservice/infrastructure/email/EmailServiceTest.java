package hg.userservice.infrastructure.email;

import com.example.commonproject.exception.InternalServerException;
import hg.userservice.core.vo.KeyType;
import hg.userservice.infrastructure.redis.RedisManager;
import hg.userservice.testconfiguration.EmbeddedRedisConfiguration;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static hg.userservice.core.vo.KeyType.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {
    @Mock
    JavaMailSender mailSender;
    @Mock
    RedisManager redisManager;

    @Spy
    @InjectMocks
    EmailService emailService;

    @Test
    @DisplayName("메일을 보낼 때, 수신자는 null이면 예외가 발생한다.")
    void receiver_and_title_and_key_must_be_not_null() {
        // given
        String receiver = createRandomUUID();

        // Case 1: when receiver is null
        assertThatThrownBy(() -> emailService.sendCode(receiver))
                .isInstanceOf(InternalServerException.class);
    }

    @Test
    @DisplayName("메일을 보낼 때, 예외 발생하면 오류를 반환해야 한다.")
    void invalid_send_email_when_sender_throws_exception() {
        // given
        String receiver = createRandomUUID();

        // when
        doThrow(new RuntimeException()).when(mailSender).send(any(MimeMessage.class));

        // then
        assertThatThrownBy(() -> emailService.sendCode(receiver))
                .isInstanceOf(InternalServerException.class);
    }

    @Test
    @DisplayName("정상적으로 메일을 보내면, 예외가 발생하지 않고, send() 메소드가 1번 호출된다.")
    void valid_send_email_then_call_method() {
        // given
        String receiver = createRandomUUID();
        MimeMessage mockMessage = mock(MimeMessage.class);

        // when
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);
        emailService.sendCode(receiver);

        // then
        verify(mailSender, times(1))
                .send(mockMessage);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}