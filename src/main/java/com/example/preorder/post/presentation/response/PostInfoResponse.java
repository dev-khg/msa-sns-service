package com.example.preorder.post.presentation.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostInfoResponse {
    private String username;
    private Long postId;
    private Long userId;

    public PostInfoResponse(String username, Long postId, Long userId) {
        this.username = username;
        this.postId = postId;
        this.userId = userId;
    }
}
