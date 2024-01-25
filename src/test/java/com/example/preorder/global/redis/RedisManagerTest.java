package com.example.preorder.global.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class RedisManagerTest {
    @Autowired
    RedisManager redisManager;

    @DisplayName("레디스 저장 후, 값은 정상적으로 조회되어야 한다.")
    @Test
    void redis_input() {
        //given
        String randomKey = UUID.randomUUID().toString();
        String randomValue = UUID.randomUUID().toString();
        redisManager.putValue(randomKey, randomValue, 1000000);

        //when
        Optional<String> value = redisManager.getValue(randomKey);

        //then
        assertThat(value).isPresent();
        assertThat(value.get()).isEqualTo(randomValue);
    }

    @DisplayName("레디스 저장 후, 삭제하면, 조회시 값이 존재하지 않는다.")
    @Test
    void redis_delete() {
        //given
        String randomKey = UUID.randomUUID().toString();
        String randomValue = UUID.randomUUID().toString();
        redisManager.putValue(randomKey, randomValue, 1000000);
        Optional<String> value = redisManager.getValue(randomKey);
        assertThat(value).isPresent();

        //when
        redisManager.deleteKey(randomKey);

        //then
        assertThat(redisManager.getValue(randomKey)).isEmpty();
    }
}