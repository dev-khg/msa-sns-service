package com.example.preorder.post.presentation.response;

import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.post.core.entity.PostLikeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.preorder.activity.core.entity.ActivityType.COMMENT;
import static com.example.preorder.activity.core.entity.ActivityType.POST_LIKE;

@Getter
@NoArgsConstructor
public class PostLikeInfoResponse implements ActivityResponse {
    private Long userId;
    private Long postId;
    private String postUsername;
    private LocalDateTime createdAt;
    private String activityType = POST_LIKE.name();

    public PostLikeInfoResponse(PostLikeEntity entity) {
        this.userId = entity.getPostEntity().getUserEntity().getId();
        this.postUsername = entity.getPostEntity().getUserEntity().getUsername();
        this.postId = entity.getPostEntity().getId();
        this.createdAt = entity.getCreatedAt();
    }
}
