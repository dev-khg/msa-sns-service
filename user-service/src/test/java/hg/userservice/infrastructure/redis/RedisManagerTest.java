package hg.userservice.infrastructure.redis;

import hg.userservice.core.vo.KeyType;
import hg.userservice.testconfiguration.EmbeddedRedisConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Import({EmbeddedRedisConfiguration.class})
class RedisManagerTest {
    @Autowired
    RedisManager redisManager;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void beforeEach() {
        stringRedisTemplate.keys("*")
                .forEach(key -> stringRedisTemplate.delete(key));
    }

    @DisplayName("존재하지 않는 레디스 키 값 조회 시, 빈 값이 반환되어야 한다.")
    @ParameterizedTest
    @EnumSource(KeyType.class)
    void success_redis_find_not_exists_key(KeyType type) {
        // given
        String notExistsIdentifier = createRandomUUID();
        // when

        // then
        assertTrue(redisManager.getValue(type, notExistsIdentifier).isEmpty());
    }

    @DisplayName("레디스 저장 후, 값은 정상적으로 조회되어야 한다.")
    @ParameterizedTest
    @EnumSource(KeyType.class)
    void success_redis_input(KeyType type) {
        //given
        String randomKey = UUID.randomUUID().toString();
        String randomValue = UUID.randomUUID().toString();
        redisManager.putValue(type, randomKey, randomValue);

        //when
        Optional<String> value = redisManager.getValue(type, randomKey);

        //then
        assertThat(value).isPresent();
        assertThat(value.get()).isEqualTo(randomValue);
    }

    @DisplayName("레디스 저장 후, 삭제하면, 조회시 값이 존재하지 않는다.")
    @ParameterizedTest
    @EnumSource(KeyType.class)
    void success_redis_delete(KeyType type) {
        //given
        String randomKey = UUID.randomUUID().toString();
        String randomValue = UUID.randomUUID().toString();
        redisManager.putValue(type, randomKey, randomValue);
        Optional<String> value = redisManager.getValue(type, randomKey);
        assertThat(value).isPresent();

        //when
        redisManager.deleteKey(type, randomKey);

        //then
        assertThat(redisManager.getValue(type, randomKey)).isEmpty();
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}