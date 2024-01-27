package com.example.preorder.follow.core.event;

import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.follow.core.entity.FollowStatus;
import com.example.preorder.follow.core.event.list.FollowDomainEvent;
import com.example.preorder.follow.core.event.list.FollowEvent;
import com.example.preorder.user.core.entity.UserEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static com.example.preorder.follow.core.event.list.FollowEvent.*;

@Component
public class FollowEventPublisher extends EventPublisher {
    public FollowEventPublisher(ApplicationEventPublisher publisher) {
        super(publisher);
    }

    public void followUpdateEvent(UserEntity follower, UserEntity followee, FollowStatus status) {
        publishEvent(
                new FollowDomainEvent(
                        FOLLOW,
                        follower.getId(),
                        followee.getId(),
                        status
                )
        );
    }
}
