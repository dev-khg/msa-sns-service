package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.comment.core.event.list.CommentDomainEvent;
import com.example.preorder.comment.core.event.list.CommentLikeEvent;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityType.*;

@Component
@Slf4j
public class CommentEventHandler extends EventHandler {

    public CommentEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        CommentDomainEvent domainEvent = (CommentDomainEvent) event;

        switch (domainEvent.getEvent()) {
            case ENROLL_COMMENT -> handleEnrollEvent(domainEvent);
            default -> log.warn("Not Handled Event | Type [{}] / ", domainEvent.getEvent());
        }
    }

    private void handleEnrollEvent(CommentDomainEvent domainEvent) {
        ActivityEntity activityEntity = create(
                domainEvent.getUserId(),
                domainEvent.getCommentId(),
                COMMENT
        );
        activityRepository.save(activityEntity);
    }

    @Override
    public EventType supportedType() {
        return EventType.COMMENT;
    }
}
