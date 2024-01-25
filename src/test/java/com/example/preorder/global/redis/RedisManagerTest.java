package com.example.preorder.global.redis;

import com.example.preorder.common.utils.RedisKeyGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static com.example.preorder.common.utils.RedisKeyGenerator.*;
import static com.example.preorder.common.utils.RedisKeyGenerator.RedisKeyType.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class RedisManagerTest {
    @Autowired
    RedisManager redisManager;

    @DisplayName("레디스 저장 후, 값은 정상적으로 조회되어야 한다.")
    @ParameterizedTest
    @EnumSource(RedisKeyType.class)
    void redis_input(RedisKeyType type) {
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
    @EnumSource(RedisKeyType.class)
    void redis_delete(RedisKeyType type) {
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
}