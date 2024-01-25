package com.example.preorder.global.redis;

import com.example.preorder.common.utils.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.preorder.common.utils.RedisKeyGenerator.*;
import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class RedisManager {
    private final StringRedisTemplate stringRedisTemplate;

    public void putValue(RedisKeyType type, String name, String value) {
        stringRedisTemplate.opsForValue().set(
                generateKey(type, name),
                value,
                ofMillis(type.getExpiration())
        );
    }

    public Optional<String> getValue(RedisKeyType type, String name) {
        return ofNullable(stringRedisTemplate.opsForValue().get(generateKey(type, name)));
    }

    public void deleteKey(RedisKeyType type, String name) {
        stringRedisTemplate.delete(generateKey(type, name));
    }
}
