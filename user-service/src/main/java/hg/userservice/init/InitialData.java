package hg.userservice.init;

import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitialData implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        UserEntity userEntity = UserEntity.create("test1234@test.com", "test1234", passwordEncoder.encode("test1234"));
        userRepository.save(userEntity);
    }
}
