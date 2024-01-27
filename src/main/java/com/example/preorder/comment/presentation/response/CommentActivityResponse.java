package com.example.preorder.comment.presentation.response;

import com.example.preorder.comment.core.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentActivityResponse {
    private Long postId;
    private Long userId;
    private String username;
    private String postUsername;

    public CommentActivityResponse(CommentEntity commentEntity) {
        this.postId = commentEntity.getPostEntity().getId();
        this.userId = commentEntity.getUserEntity().getId();
        this.username = commentEntity.getUserEntity().getUsername();
        this.postUsername = commentEntity.getPostEntity().getUserEntity().getUsername();
    }
}
