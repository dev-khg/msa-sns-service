package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import com.example.preorder.post.core.event.list.PostLikeDomainEvent;
import org.springframework.stereotype.Component;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityStatus.*;
import static com.example.preorder.activity.core.entity.ActivityType.*;

@Component
public class PostLikeEventHandler extends EventHandler {
    public PostLikeEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        PostLikeDomainEvent domainEvent = (PostLikeDomainEvent) event;

        switch (domainEvent.getEvent()) {
            case LIKE -> handlePostLikeEvent(domainEvent);
            case UNLIKE -> handlePostUnLikeEvent(domainEvent);
        }
    }

    private void handlePostLikeEvent(PostLikeDomainEvent domainEvent) {
        ActivityEntity activityEntity = create(domainEvent.getUserId(), domainEvent.getPostId(), POST_LIKE);
        activityRepository.save(activityEntity);
    }

    private void handlePostUnLikeEvent(PostLikeDomainEvent domainEvent) {
        activityRepository.changeStatus(
                domainEvent.getUserId(),
                domainEvent.getPostId(),
                POST_LIKE,
                INVALID
        );
    }

    @Override
    public EventType supportedType() {
        return EventType.POST_LIKE;
    }
}
