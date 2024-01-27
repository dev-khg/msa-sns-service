package com.example.preorder.common.event;

public interface Event {
    EventType getEventType();

    Integer getOrdinal();

    Long getPublisher();

    Object getData();
}
