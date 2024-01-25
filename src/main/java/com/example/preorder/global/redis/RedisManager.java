package com.example.preorder.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.time.Duration.ofMillis;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class RedisManager {
    private final StringRedisTemplate stringRedisTemplate;

    public void putValue(String key, String value, long milliseconds) {
        stringRedisTemplate.opsForValue().set(key, value, ofMillis(milliseconds));
    }

    public Optional<String> getValue(String key) {
        return ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    public void deleteKey(String key) {
        stringRedisTemplate.unlink(key);
    }
}
