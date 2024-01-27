package com.example.preorder.common.event;

public interface Event {
    EventType eventType();

    Integer ordinal();

    Object data();
}
