package com.example.preorder.comment.presentation.response;

import com.example.preorder.comment.core.entity.CommentLikeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentLikeActivityResponse {
    private Long commentId;
    private Long postId;
    private String postUsername;
    private String username;

    public CommentLikeActivityResponse(CommentLikeEntity entity) {
        this.commentId = entity.getCommentEntity().getId();
        this.postId = entity.getCommentEntity().getPostEntity().getId();
        this.username = entity.getUserEntity().getUsername();
        this.postUsername = entity.getCommentEntity().getPostEntity().getUserEntity().getUsername();
    }
}
