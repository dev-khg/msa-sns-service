package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import org.springframework.stereotype.Component;

@Component
public class PostEventHandler extends EventHandler {
    public PostEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {

    }

    @Override
    public EventType supportedType() {
        return EventType.POST;
    }
}
