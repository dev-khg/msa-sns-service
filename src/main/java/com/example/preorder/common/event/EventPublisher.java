package com.example.preorder.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public abstract class EventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishEvent(Event event) {
        publisher.publishEvent(event);
    }
}
