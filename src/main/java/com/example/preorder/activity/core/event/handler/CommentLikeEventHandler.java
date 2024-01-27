package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.comment.core.event.list.CommentLikeDomainEvent;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentLikeEventHandler extends EventHandler {
    public CommentLikeEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        CommentLikeDomainEvent domainEvent = (CommentLikeDomainEvent) event;
        switch (domainEvent.getEvent()) {
            case LIKE -> {
                System.out.println("a");
                break;
            }
            case UNLIKE -> {
                System.out.println();
                break;
            }
            default -> log.warn("Not Handled Event | Type [{}] / ", domainEvent.getEvent());
        }
    }

    @Override
    public EventType supportedType() {
        return EventType.COMMENT_LIKE;
    }
}
