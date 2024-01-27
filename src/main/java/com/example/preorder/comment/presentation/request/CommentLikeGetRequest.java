package com.example.preorder.comment.presentation.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeGetRequest {
    private List<Long> commentLikeIdList;
}
