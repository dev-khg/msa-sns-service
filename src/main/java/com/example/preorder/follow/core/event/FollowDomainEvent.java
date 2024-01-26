package com.example.preorder.follow.core.event;

import com.example.preorder.common.event.Event;
import com.example.preorder.follow.core.vo.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowDomainEvent implements Event, Serializable {
    private Long followerId;
    private Long followeeId;
    private FollowStatus status;
}
