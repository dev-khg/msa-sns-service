package com.example.newsfeedservice.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {
    @NotBlank(message = "content must be not null")
    @Size(max = 200, message = "content must be small than 200 letters.")
    private String content;
}
