package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityKey;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.common.event.EventType;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    ActivityEntity save(ActivityEntity activity);

    Optional<ActivityEntity> findById(Long id);

    List<ActivityEntity> findByEventTypeAndOrdinalAndTargetId(EventType eventType,
                                                              Integer ordinal,
                                                              Long targetId,
                                                              ActivityStatus status);
}
