package com.example.hg.infra.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.hg.infra.redis.KeyType.generation;
import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class RedisManager {
    private final StringRedisTemplate stringRedisTemplate;

    public void putValue(KeyType type, String key, String value) {
        String redisKey = generation(type, key);
        stringRedisTemplate.opsForValue().set(redisKey, value, ofMillis(type.getExpiration()));
    }

    public Optional<String> getValue(KeyType type, String identifier) {
        String redisKey = generation(type, identifier);
        return ofNullable(stringRedisTemplate.opsForValue().get(redisKey));
    }

    public void deleteKey(KeyType type, String identifier) {
        String redisKey = generation(type, identifier);
        stringRedisTemplate.delete(redisKey);
    }

}

