package com.example.preorder.post.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeGetRequest {
    @NotNull(message = "postLikeIdList must be not null")
    private List<Long> postLikeIdList;
}
