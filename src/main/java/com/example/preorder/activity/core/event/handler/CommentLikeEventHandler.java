package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.comment.core.event.list.CommentLikeDomainEvent;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityStatus.*;
import static com.example.preorder.activity.core.entity.ActivityType.*;

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
            case LIKE -> handleLikeEvent(domainEvent);
            case UNLIKE -> handleUnlikeEvent(domainEvent);
            default -> log.warn("Not Handled Event | Type [{}] / ", domainEvent.getEvent());
        }
    }

    private void handleLikeEvent(CommentLikeDomainEvent domainEvent) {
        ActivityEntity activityEntity =
                activityRepository.findByUserIdAndTargetIdAndTypeAndStatus(
                        domainEvent.getUserId(),
                        domainEvent.getCommentId(),
                        COMMENT_LIKE, VALID
                ).orElse(null);

        if (activityEntity == null) {
            activityEntity = create(
                    domainEvent.getUserId(),
                    domainEvent.getCommentId(),
                    COMMENT_LIKE
            );
            activityRepository.save(activityEntity);
        }
    }

    private void handleUnlikeEvent(CommentLikeDomainEvent domainEvent) {
        activityRepository.changeStatus(
                domainEvent.getUserId(),
                domainEvent.getCommentId(),
                COMMENT_LIKE,
                INVALID
        );
    }

    @Override
    public EventType supportedType() {
        return EventType.COMMENT_LIKE;
    }
}
