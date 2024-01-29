package com.example.preorder.init;

import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.user.core.entity.UserEntity.*;

@Component
@RequiredArgsConstructor
public class InitUserData implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        List<UserEntity> users = userRepository.findAll();

        String queryString = "update UserEntity u set u.password= :password where u.id = :user_id";

        for (UserEntity user : users) {
            em.createQuery(queryString)
                    .setParameter("user_id", user.getId())
                    .setParameter("password", passwordEncoder.encode(user.getPassword()))
                    .executeUpdate();
        }
    }
}
