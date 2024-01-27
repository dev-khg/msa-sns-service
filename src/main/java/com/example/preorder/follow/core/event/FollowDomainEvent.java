package com.example.preorder.follow.core.event;

import com.example.preorder.common.event.Event;
import com.example.preorder.common.event.EventType;
import com.example.preorder.follow.core.event.request.FollowData;
import com.example.preorder.follow.core.vo.FollowEvent;
import com.example.preorder.follow.core.vo.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowDomainEvent implements Event, Serializable {
    private FollowEvent followEvent;
    private FollowData followerData;

    public FollowDomainEvent(FollowData followerData, FollowEvent event) {
        this.followerData = followerData;
        this.followEvent = event;
    }

    @Override
    public EventType eventType() {
        return EventType.FOLLOW;
    }

    @Override
    public Integer ordinal() {
        return followEvent.ordinal();
    }

    @Override
    public Object data() {
        return followerData;
    }
}
