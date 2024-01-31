package com.example.newsfeedservice.core.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostActivityDTO {
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
}
