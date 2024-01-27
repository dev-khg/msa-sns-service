package com.example.preorder.post.db.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostDto {
    private Long postId;
    private Long userId;
    private String username;

    @QueryProjection

    public PostDto(Long postId, Long userId, String username) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
    }
}
