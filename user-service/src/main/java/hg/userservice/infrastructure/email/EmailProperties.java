package hg.userservice.infrastructure.email;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProperties extends Properties {
    @PostConstruct
    private void init() {
        this.put("mail.smtp.auth", true);
        this.put("mail.smtp.starttls.enable", true);
        this.put("mail.smtp.starttls.required", true);
        this.put("mail.smtp.connectiontimeout", 5000);
        this.put("mail.smtp.timeout", 5000);
        this.put("mail.smtp.writetimeout", 5000);
    }
}