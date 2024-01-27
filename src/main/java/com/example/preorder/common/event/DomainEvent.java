package com.example.preorder.common.event;

import lombok.Getter;

@Getter
public abstract class DomainEvent {
    private final EventType eventType;

    public DomainEvent(EventType eventType) {
        this.eventType = eventType;
    }
}
