package com.example.preorder.activity.presentation.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ActivityGetRequest {
    private List<Long> followerList;
}
