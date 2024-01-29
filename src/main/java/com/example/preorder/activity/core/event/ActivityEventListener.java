package com.example.preorder.activity.core.event;

import com.example.preorder.activity.core.event.handler.EventHandler;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ActivityEventListener {
    private Map<EventType, EventHandler> handlerMap;

    public ActivityEventListener(List<EventHandler> handlers) {
        init(handlers);
    }

    @TransactionalEventListener
    public void followEventHandler(DomainEvent event) {
        handlerMap.get(event.getEventType()).dispatch(event);
    }

    private void init(List<EventHandler> handlers) {
        handlerMap = new HashMap<>();

        handlers.forEach(
                handler -> handlerMap.put(handler.supportedType(), handler)
        );
    }

}
