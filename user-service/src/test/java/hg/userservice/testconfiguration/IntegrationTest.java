package hg.userservice.testconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.repository.UserRepository;
import hg.userservice.infrastructure.jwt.TokenProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Disabled
@Transactional
@SpringBootTest
@Import({EmbeddedRedisConfiguration.class})
@AutoConfigureMockMvc
public abstract class IntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected KeyValueStorage keyValueStorage;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected TokenProvider tokenProvider;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    protected List<UserEntity> saveUserList;

    @PersistenceContext
    private EntityManager em;

    @BeforeEach
    void beforeEach() {
        settingUserEntities();
        clearKeyValueStorage();
        flushAndClearPersistence();
    }

    private void clearKeyValueStorage() {
        stringRedisTemplate.keys("*")
                .forEach(key -> stringRedisTemplate.delete(key));
    }

    private void settingUserEntities() {
        saveUserList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserEntity userEntity = createUserEntity();
            userRepository.save(userEntity);
            saveUserList.add(userEntity);
        }
    }

    protected String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    protected void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    protected UserEntity createUserEntity() {
        return UserEntity.create(createRandomUUID(), createRandomUUID(), createRandomUUID());
    }
}
