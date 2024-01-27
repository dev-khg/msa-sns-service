package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventType;
import com.example.preorder.post.core.event.list.PostDomainEvent;
import org.springframework.stereotype.Component;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityType.*;

@Component
public class PostEventHandler extends EventHandler {
    public PostEventHandler(ActivityRepository activityRepository) {
        super(activityRepository);
    }

    @Override
    public void dispatch(DomainEvent event) {
        PostDomainEvent domainEvent = (PostDomainEvent) event;

        switch (domainEvent.getPostEvent()){
            case POST_CREATED -> handlePostCreatedEvent(domainEvent);
        }
    }

    private void handlePostCreatedEvent(PostDomainEvent domainEvent) {
        ActivityEntity activityEntity = create(domainEvent.getUserId(), domainEvent.getPostId(), POST);
        activityRepository.save(activityEntity);
    }

    @Override
    public EventType supportedType() {
        return EventType.POST;
    }
}
