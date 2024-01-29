package com.example.preorder.comment.presentation.response;

import com.example.preorder.comment.core.entity.CommentLikeEntity;
import com.example.preorder.common.activity.ActivityResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.preorder.activity.core.entity.ActivityType.COMMENT;
import static com.example.preorder.activity.core.entity.ActivityType.COMMENT_LIKE;

@Getter
@NoArgsConstructor
public class CommentLikeActivityResponse implements ActivityResponse {
    private Long commentId;
    private Long postId;
    private String postUsername;
    private String username;
    private LocalDateTime createdAt;
    private String activityType = COMMENT_LIKE.name();

    public CommentLikeActivityResponse(CommentLikeEntity entity) {
        this.commentId = entity.getCommentEntity().getId();
        this.postId = entity.getCommentEntity().getPostEntity().getId();
        this.username = entity.getUserEntity().getUsername();
        this.postUsername = entity.getCommentEntity().getPostEntity().getUserEntity().getUsername();
        this.createdAt = entity.getCreatedAt();
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
}
