package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class EventHandler {
    protected final ActivityRepository activityRepository;

    public EventHandler(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public abstract void dispatch(DomainEvent event);

    public abstract EventType supportedType();
}
