package com.example.preorder.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
public abstract class EventPublisher {
    private final ApplicationEventPublisher publisher;

    @TransactionalEventListener
    public   <T extends DomainEvent> void publishEvent(T event) {
        publisher.publishEvent(event);
    }
}
