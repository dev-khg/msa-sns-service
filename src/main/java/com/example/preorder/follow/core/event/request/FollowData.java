package com.example.preorder.follow.core.event.request;

import com.example.preorder.follow.core.vo.FollowStatus;
import lombok.Getter;

import java.io.Serializable;

public record FollowData(Long followerId, Long followeeId, FollowStatus status) implements Serializable {
}
