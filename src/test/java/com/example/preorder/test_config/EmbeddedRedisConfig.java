package com.example.preorder.test_config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

@ActiveProfiles("test")
@Configuration
public class EmbeddedRedisConfig {
    private final RedisServer redisServer;

    public EmbeddedRedisConfig(@Value("${spring.data.redis.port}") int port) {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    private void init() {
        try {
            redisServer.start();
        } catch (Exception e) {
        }
    }

    @PreDestroy
    private void destroy() {
        redisServer.stop();
    }
}

