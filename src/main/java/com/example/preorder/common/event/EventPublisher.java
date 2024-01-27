package com.example.preorder.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public abstract class EventPublisher {
    private final ApplicationEventPublisher publisher;

    protected  <T extends DomainEvent> void publishEvent(T event) {
        publisher.publishEvent(event);
    }
}
