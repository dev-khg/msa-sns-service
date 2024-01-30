package hg.userservice.infrastructure.redis;

import hg.userservice.core.repository.KeyValueStorage;
import hg.userservice.core.vo.KeyType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;

import static hg.userservice.core.vo.KeyType.*;
import static java.time.Duration.*;
import static java.util.Optional.*;

@Service
@RequiredArgsConstructor
public class RedisManager implements KeyValueStorage {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void putValue(KeyType type, String key, String value) {
        String redisKey = generation(type, key);
        stringRedisTemplate.opsForValue().set(redisKey, value, ofMillis(type.getExpiration()));
    }

    @Override
    public Optional<String> getValue(KeyType type, String identifier) {
        String redisKey = generation(type, identifier);
        return ofNullable(stringRedisTemplate.opsForValue().get(redisKey));
    }

    @Override
    public void deleteKey(KeyType type, String identifier) {
        String redisKey = generation(type, identifier);
        stringRedisTemplate.delete(redisKey);
    }

}
