package com.example.preorder.common.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.example.preorder.common.constant.TimeUnits.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKeyGenerator {
    @Getter
    public enum RedisKeyType {
        EMAIL_CERT("EMAIL", ONE_MINUTE * 5),
        REFRESH_TOKEN("REFRESH", ONE_DAY * 7),
        BLACKLIST_TOKEN("BLACK", ONE_MINUTE * 30);

        private final String key;
        private final long expiration;

        RedisKeyType(String key, long expiration) {
            this.key = key;
            this.expiration = expiration;
        }
    }

    public static String generateKey(RedisKeyType type, Object obj) {
        return String.format("%s:%s", type.getKey(), obj);
    }

}
