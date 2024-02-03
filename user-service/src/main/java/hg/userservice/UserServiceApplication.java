package hg.userservice;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableJpaAuditing
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Component
    @RequiredArgsConstructor
    static class InitData implements ApplicationRunner {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final EntityManager em;

        @Override
        @Transactional
        public void run(ApplicationArguments args) throws Exception {
            List<UserEntity> users = userRepository.findAll();

            String queryString = "update UserEntity u set u.password = :password where u.id = :user_number";

            for (UserEntity user : users) {
                em.createQuery(queryString)
                        .setParameter("user_number", user.getId())
                        .setParameter("password", passwordEncoder.encode(user.getPassword()))
                        .executeUpdate();
            }
        }
    }
}
