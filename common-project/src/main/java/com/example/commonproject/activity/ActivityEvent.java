package com.example.commonproject.activity;

import lombok.Getter;

@Getter
public class ActivityEvent {
    private ActivityType type;
    private Long userId;
    private Long targetId;

    public ActivityEvent(ActivityType type, Long userId, Long targetId) {
        this.type = type;
        this.userId = userId;
        this.targetId = targetId;
    }
}
