package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityType;

import java.util.List;

public interface ActivityRepository {

    List<ActivityEntity> findValidActivitiesByUserId(List<Long> userId);

    void changeInvalidStatus(Long targetId, ActivityType type);
}
