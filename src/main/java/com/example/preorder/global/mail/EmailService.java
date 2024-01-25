package com.example.preorder.global.mail;

import com.example.preorder.common.exception.InternalErrorException;
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

    public void sendEmail(String receiver, String title, String code) {
        Objects.requireNonNull(receiver, "receiver must be not null");
        Objects.requireNonNull(title, "receiver must be not null");
        Objects.requireNonNull(code, "receiver must be not null");

        try {
            MimeMessage message = createMessage(receiver, title, code);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Fail to send email. receiver = {}, title = {}, code = {}", receiver, title, code, e);
            throw new InternalErrorException("fail to send email");
        }
    }

    private MimeMessage createMessage(String receiver, String title, String key) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, receiver);
        message.setSubject(title);

        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:20px;'>");
        sb.append("<h3>회원가입 인증 코드입니다.</h3>");
        sb.append("<div style='font-size:130%'>");
        sb.append("CODE : <strong>");
        sb.append(key);
        sb.append("</strong><div><br/>");
        sb.append("</div>");

        message.setText(sb.toString(), "utf-8", "html");
        message.setFrom(new InternetAddress("kwonmailsender@gmail.com", "권홍근"));

        return message;
    }
}
