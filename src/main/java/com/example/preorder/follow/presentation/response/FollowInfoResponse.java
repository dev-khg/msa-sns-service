package com.example.preorder.follow.presentation.response;

import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.follow.core.entity.FollowEntity;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.example.preorder.activity.core.entity.ActivityType.FOLLOW;

@Getter
public class FollowInfoResponse implements ActivityResponse {
    private Long followerId;
    private Long followeeId;
    private String followerName;
    private String followeeName;
    private LocalDateTime createdAt;
    private String activityType = FOLLOW.name();

    public FollowInfoResponse(FollowEntity followEntity) {
        this.followerId = followEntity.getFollower().getId();
        this.followeeId = followEntity.getFollowee().getId();
        this.followerName = followEntity.getFollower().getUsername();
        this.followeeName = followEntity.getFollowee().getUsername();
        this.createdAt = followEntity.getCreatedAt();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
