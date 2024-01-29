package com.example.preorder.comment.presentation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentGetRequest {
    @NotNull(message = "commentIdList must be not null")
    private List<Long> commentIdList;
}
