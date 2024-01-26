package com.example.preorder.activity.db;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityKey;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.repository.ActivityRepository;
import com.example.preorder.common.event.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActivityRepositoryImpl implements ActivityRepository {
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityEntity save(ActivityEntity activity) {
        return activityJpaRepository.save(activity);
    }

    @Override
    public Optional<ActivityEntity> findById(Long id) {
        return activityJpaRepository.findById(id);
    }

    @Override
    public List<ActivityEntity> findByEventTypeAndOrdinalAndTargetId(
            EventType eventType,
            Integer ordinal,
            Long targetId,
            ActivityStatus status
    ) {
        return activityJpaRepository.findByConditions(eventType, ordinal, targetId, status);
    }
}
