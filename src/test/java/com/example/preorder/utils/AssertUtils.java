package com.example.preorder.utils;


import org.junit.jupiter.api.Assertions;

public final class AssertUtils {
    private AssertUtils() {
    }

    public static void assertNotNulls(Object... args) {
        for (Object arg : args) {
            Assertions.assertNotNull(arg);
        }
    }

    public static void assertNulls(Object... args) {
        for (Object arg : args) {
            Assertions.assertNull(arg);
        }
    }
}
