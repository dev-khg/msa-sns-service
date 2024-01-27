package com.example.preorder.post.presentation.response;

import com.example.preorder.post.core.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String username;

    public static CommentResponse of(CommentEntity commentEntity) {
        return new CommentResponse(commentEntity.getId(), commentEntity.getUserId(), commentEntity.getUsername());
    }
}
