package com.example.hg.infra.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KeyType {
    EMAIL_CERT("EMAIL", 1000L * 60 * 5), // 5Min
    REFRESH_TOKEN("REFRESH", 1000L * 60 * 60 * 24 * 7), // 7Days
    BLACKLIST_TOKEN("BLACK", 1000L * 60 * 30); // 30Min

    private final String key;
    private final long expiration;

    public static String generation(KeyType type, Object obj) {
        return String.format("%s:%s", type.getKey(), obj);
    }
}
