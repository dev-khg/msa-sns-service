package com.example.preorder.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AssertionsUtils {

    public static void hasText(RuntimeException e, String... args) {
        
        throw e;
    }
}
