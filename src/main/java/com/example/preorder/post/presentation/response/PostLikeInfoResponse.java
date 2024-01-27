package com.example.preorder.post.presentation.response;

import com.example.preorder.post.core.entity.PostLikeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLikeInfoResponse {
    private Long userId;
    private Long postId;
    private String postUsername;

    public PostLikeInfoResponse(PostLikeEntity entity) {
        this.userId = entity.getPostEntity().getUserEntity().getId();
        this.postUsername = entity.getPostEntity().getUserEntity().getUsername();
        this.postId = entity.getPostEntity().getId();
    }
}
