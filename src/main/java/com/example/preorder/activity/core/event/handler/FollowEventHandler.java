package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.comment.core.event.list.CommentLikeDomainEvent;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import com.example.preorder.follow.core.event.list.FollowDomainEvent;
import org.springframework.stereotype.Component;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityEntity.create;
import static com.example.preorder.activity.core.entity.ActivityStatus.INVALID;
import static com.example.preorder.activity.core.entity.ActivityStatus.VALID;
import static com.example.preorder.activity.core.entity.ActivityType.COMMENT_LIKE;
import static com.example.preorder.activity.core.entity.ActivityType.FOLLOW;

@Component
public class FollowEventHandler extends EventHandler {

    public FollowEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        FollowDomainEvent domainEvent = (FollowDomainEvent) event;

        // TODO : [Refactor] follower event 세분화
        switch (domainEvent.getStatus()) {
            case FOLLOWING -> handleFollow(domainEvent);
            case UNFOLLOWING -> handleUnFollow(domainEvent);
        }
    }

    private void handleFollow(FollowDomainEvent domainEvent) {
        ActivityEntity activityEntity = create(domainEvent.getFollowerId(), domainEvent.getFolloweeId(), FOLLOW);
        activityRepository.save(activityEntity);
    }

    private void handleUnFollow(FollowDomainEvent domainEvent) {
        activityRepository.changeStatus(
                domainEvent.getFollowerId(),
                domainEvent.getFolloweeId(),
                FOLLOW,
                INVALID
        );
    }

    @Override
    public EventType supportedType() {
        return EventType.FOLLOW;
    }
}
