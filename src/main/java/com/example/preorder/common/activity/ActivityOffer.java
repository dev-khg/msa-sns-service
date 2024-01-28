package com.example.preorder.common.activity;

import java.util.List;

public interface ActivityOffer {

    List<ActivityResponse> handleActivity(List<Long> targetIdList);
}
