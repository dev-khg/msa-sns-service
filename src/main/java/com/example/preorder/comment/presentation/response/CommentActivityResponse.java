package com.example.preorder.comment.presentation.response;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.common.activity.ActivityResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentActivityResponse implements ActivityResponse {
    private Long postId;
    private Long userId;
    private String username;
    private String postUsername;
    private LocalDateTime createdAt;

    public CommentActivityResponse(CommentEntity commentEntity) {
        this.postId = commentEntity.getPostEntity().getId();
        this.userId = commentEntity.getUserEntity().getId();
        this.username = commentEntity.getUserEntity().getUsername();
        this.postUsername = commentEntity.getPostEntity().getUserEntity().getUsername();
        this.createdAt = commentEntity.getCreatedAt();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
