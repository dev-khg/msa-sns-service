package com.example.preorder.post.presentation.response;

import com.example.preorder.common.activity.ActivityResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PostInfoResponse implements ActivityResponse {
    private String username;
    private Long postId;
    private Long userId;
    private LocalDateTime createAt;

    public PostInfoResponse(String username, Long postId, Long userId, LocalDateTime createdAt) {
        this.username = username;
        this.postId = postId;
        this.userId = userId;
        this.createAt = createdAt;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createAt;
    }
}
