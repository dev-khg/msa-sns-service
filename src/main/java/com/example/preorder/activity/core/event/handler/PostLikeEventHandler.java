package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import com.example.preorder.post.core.event.list.PostLikeDomainEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityStatus.*;
import static com.example.preorder.activity.core.entity.ActivityType.*;

@Component
@Transactional
public class PostLikeEventHandler extends EventHandler {
    public PostLikeEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        PostLikeDomainEvent domainEvent = (PostLikeDomainEvent) event;

        switch (domainEvent.getEvent()) {
            case LIKE_IT -> handlePostLikeEvent(domainEvent);
            case UNLIKE_IT -> handlePostUnLikeEvent(domainEvent);
        }
    }

    private void handlePostLikeEvent(PostLikeDomainEvent domainEvent) {
        ActivityEntity activityEntity = create(domainEvent.getUserId(), domainEvent.getPostId(), POST_LIKE);
        activityRepository.save(activityEntity);
    }

    protected void handlePostUnLikeEvent(PostLikeDomainEvent domainEvent) {
        Optional<ActivityEntity> byUserIdAndTargetIdAndTypeAndStatus = activityRepository.findByUserIdAndTargetIdAndTypeAndStatus(domainEvent.getUserId(), domainEvent.getPostId(), POST_LIKE, VALID);
        byUserIdAndTargetIdAndTypeAndStatus.ifPresent(arg -> arg.changeStatus(INVALID));
    }

    @Override
    public EventType supportedType() {
        return EventType.POST_LIKE;
    }
}
