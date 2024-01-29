package com.example.preorder.comment.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeGetRequest {
    @NotNull(message = "commentLikeIdList must be not null")
    private List<Long> commentLikeIdList;
}
