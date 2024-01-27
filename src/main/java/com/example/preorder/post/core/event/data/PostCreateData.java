package com.example.preorder.post.core.event.data;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateData {
    private Long userId;
    private Long postId;
}
