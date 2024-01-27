package com.example.preorder.common.event;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DomainEvent implements Event{
    private final EventType eventType;
    private final Integer ordinal;
    private final Object data;

    @Override
    public EventType eventType() {
        return eventType;
    }

    @Override
    public Integer ordinal() {
        return ordinal;
    }

    @Override
    public Object data() {
        return data;
    }
}
