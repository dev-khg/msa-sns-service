package com.example.preorder.activity.core.event.handler;

import com.example.preorder.common.event.Event;
import com.example.preorder.common.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.*;

@RequiredArgsConstructor
public abstract class EventHandler {
    private final EventType eventType;

    protected abstract void handle(Event event);

    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void checkHandle(Event event) {
        if (this.eventType == event.eventType()) {
            handle(event);
        }
    }
}
