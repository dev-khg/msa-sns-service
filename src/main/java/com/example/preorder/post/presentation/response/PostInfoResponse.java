package com.example.preorder.post.presentation.response;

import com.example.preorder.activity.core.entity.ActivityType;
import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.post.core.entity.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.preorder.activity.core.entity.ActivityType.COMMENT;
import static com.example.preorder.activity.core.entity.ActivityType.POST;

@Getter
@NoArgsConstructor
public class PostInfoResponse implements ActivityResponse {
    private String username;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
    private String content;
    private String activityType = POST.name();

    public PostInfoResponse(PostEntity postEntity) {
        this.username = postEntity.getUserEntity().getUsername();
        this.postId = postEntity.getId();
        this.userId = postEntity.getUserEntity().getId();
        this.createdAt = postEntity.getCreatedAt();
        this.content = postEntity.getContent();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
