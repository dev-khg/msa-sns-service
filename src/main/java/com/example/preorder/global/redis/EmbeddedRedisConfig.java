package com.example.preorder.global.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Slf4j
@Configuration
@Profile("local")
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
            log.error("Can't start embedded redis server.\nmessage = {}", e.getMessage());
        }
    }

    @PreDestroy
    private void destroy() {
        redisServer.stop();
    }
}
