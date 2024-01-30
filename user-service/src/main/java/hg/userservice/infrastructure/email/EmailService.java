package hg.userservice.infrastructure.email;

import com.example.commonproject.exception.InternalServerException;
import hg.userservice.core.repository.KeyValueStorage;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final KeyValueStorage keyValueStorage;
    private final Random random = new Random();
    private final static int minBound = 100000;
    private final static int maxBound = 999999;
    private final static int rangeBound = maxBound - minBound;

    public void sendCode(String receiver) {
        Objects.requireNonNull(receiver, "receiver must be not null");

        try {
            String code = generateSixDigitsRandomCode();
            MimeMessage message = createMessage(receiver, "[PRE-ORDER] 회원가입 인증 코드 입니다.", code);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Fail to send email.", e);
            throw new InternalServerException("fail to send email");
        }
    }

    private MimeMessage createMessage(String receiver, String title, String key) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, receiver);
        message.setSubject(title);

        String sb =
                "<div style='margin:20px;'>" +
                "<h3>회원가입 인증 코드입니다.</h3>" +
                "<div style='font-size:130%'>" +
                    "CODE : <strong>" + key + "</strong><div><br/>" +
                "</div>";

        message.setText(sb, "utf-8", "html");
        message.setFrom(new InternetAddress("kwonmailsender@gmail.com", "권홍근"));

        return message;
    }

    private String generateSixDigitsRandomCode() {
        return String.valueOf(random.nextInt(rangeBound) + minBound);
    }
}
