package com.example.preorder.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RandomGenerator {
    private static final Random random = new Random();
    private final static int minBound = 100000;
    private final static int maxBound = 999999;
    private final static int rangeBound = maxBound - minBound;

    public static int generateSixDigitsRandomCode() {
        return random.nextInt(rangeBound) + minBound;
    }
}
