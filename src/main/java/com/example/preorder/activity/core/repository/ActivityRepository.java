package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.entity.ActivityType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {

    ActivityEntity save(ActivityEntity activityEntity);

    List<ActivityEntity> findValidActivitiesByUserId(List<Long> userId, ActivityStatus status, Pageable pageable);

    Optional<ActivityEntity> findByUserIdAndTargetIdAndTypeAndStatus(Long userId,
                                                                     Long targetId,
                                                                     ActivityType type,
                                                                     ActivityStatus status);

    void changeStatus(Long userId, Long targetId, ActivityType type, ActivityStatus status);
}
