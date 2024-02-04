package com.example.commonproject.event;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;

public interface EventPublisher {

    void publish(ActivityEvent event);

    void fallback(ActivityEvent event, Exception e);
}
