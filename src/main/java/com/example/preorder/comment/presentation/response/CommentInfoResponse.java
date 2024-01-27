package com.example.preorder.comment.presentation.response;

import com.example.preorder.comment.core.entity.CommentEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentInfoResponse {
    private Long userId;
    private Long commentId;
    private String username;

    public CommentInfoResponse(CommentEntity entity) {
        this.userId = entity.getUserEntity().getId();
        this.commentId = entity.getId();
        this.username = entity.getUserEntity().getUsername();
    }
}
