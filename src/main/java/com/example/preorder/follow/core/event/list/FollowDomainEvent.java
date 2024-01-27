package com.example.preorder.follow.core.event.list;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.follow.core.entity.FollowStatus;
import lombok.Getter;

import static com.example.preorder.common.event.EventType.*;

@Getter
public class FollowDomainEvent extends DomainEvent {
    private FollowEvent followEvent;
    private Long followerId;
    private Long followeeId;
    private FollowStatus status;

    public FollowDomainEvent(FollowEvent followEvent,
                             Long followerId,
                             Long followeeId,
                             FollowStatus status) {
        super(FOLLOW);
        this.followEvent = followEvent;
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.status = status;
    }
}
