package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.comment.core.event.list.CommentDomainEvent;
import com.example.preorder.comment.core.event.list.CommentLikeEvent;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import org.springframework.stereotype.Component;

@Component
public class CommentEventHandler extends EventHandler {

    public CommentEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        CommentDomainEvent domainEvent = (CommentDomainEvent) event;

        switch (domainEvent.getEvent()) {
            case ENROLL_COMMENT -> {
                return;
            }
        }
    }

    @Override
    public EventType supportedType() {
        return EventType.COMMENT;
    }
}
