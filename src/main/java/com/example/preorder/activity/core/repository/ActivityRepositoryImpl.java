package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivityRepositoryImpl implements ActivityRepository{
    @Override
    public List<ActivityEntity> findValidActivitiesByUserId(List<Long> userId) {
        return null;
    }

    @Override
    public void changeInvalidStatus(Long targetId, ActivityType type) {

    }
}
