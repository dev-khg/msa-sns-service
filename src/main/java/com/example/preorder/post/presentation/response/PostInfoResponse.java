package com.example.preorder.post.presentation.response;

import com.example.preorder.post.core.entity.PostEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostInfoResponse {
    private Long postId;
    private String username;
    private Long userId;
    private String content;

    public static PostInfoResponse of(PostEntity postEntity) {
        return new PostInfoResponse(
                postEntity.getId(),
                postEntity.getUsername(),
                postEntity.getUserId(),
                postEntity.getContent()
        );
    }
}
