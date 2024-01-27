package com.example.preorder.post.presentation.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostLikeResponse {
    private Long userId;
    private Long postId;
    private Long postUserId;
    private String username;
    private String postUsername;

    public PostLikeResponse(Long userId, Long postId, Long postUserId, String username, String postUsername) {
        this.userId = userId;
        this.postId = postId;
        this.postUserId = postUserId;
        this.username = username;
        this.postUsername = postUsername;
    }

}
