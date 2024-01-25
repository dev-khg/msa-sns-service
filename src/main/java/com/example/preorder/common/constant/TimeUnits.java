package com.example.preorder.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeUnits {
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = ONE_SECOND * 60;
    public static final long ONE_HOUR = ONE_MINUTE * 60;
    public static final long ONE_DAY = ONE_HOUR * 24;
}
