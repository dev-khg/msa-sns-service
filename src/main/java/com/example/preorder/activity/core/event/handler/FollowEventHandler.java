package com.example.preorder.activity.core.event.handler;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityKey;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.Event;
import com.example.preorder.follow.core.event.request.FollowData;
import com.example.preorder.follow.core.vo.FollowEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.example.preorder.activity.core.entity.ActivityEntity.*;
import static com.example.preorder.activity.core.entity.ActivityStatus.*;
import static com.example.preorder.common.event.EventType.*;

@Component
public class FollowEventHandler extends EventHandler {
    private final ActivityRepository activityRepository;

    public FollowEventHandler(ActivityRepository activityRepository) {
        super(FOLLOW);
        this.activityRepository = activityRepository;
    }

    @Override
    protected void handle(Event event) {
        switch (FollowEvent.values()[event.ordinal()]) {
            case FOLLOW -> handleFollowEvent(event);
        }
    }

    @Transactional
    protected void handleFollowEvent(Event event) {
        FollowData data = (FollowData) event.data();
        ActivityKey key = ActivityKey.create(event.eventType(), event.ordinal());

//        ActivityEntity activityEntity = create(
//                key,
//                event.getPublisher(),
//                data.followeeId(),
//                data.status().ordinal(),
//                VALID
//        );

//        activityRepository.save(activityEntity);
    }
}
