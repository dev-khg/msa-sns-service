package com.example.preorder.post.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostGetRequest {
    @NotNull(message = "postIdList must be not null")
    private List<Long> postIdList;
}
