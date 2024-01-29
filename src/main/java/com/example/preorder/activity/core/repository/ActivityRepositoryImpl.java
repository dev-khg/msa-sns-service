package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.entity.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActivityRepositoryImpl implements ActivityRepository {
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityEntity save(ActivityEntity activityEntity) {
        return activityJpaRepository.saveAndFlush(activityEntity);
    }

    @Override
    public List<ActivityEntity> findValidActivitiesByUserId(List<Long> userId,
                                                            ActivityStatus status,
                                                            Pageable pageable) {
        return activityJpaRepository.findByUserIdInAndStatus(userId, status, pageable);
    }

    @Override
    public Optional<ActivityEntity> findByUserIdAndTargetIdAndTypeAndStatus(Long userId,
                                                                            Long targetId,
                                                                            ActivityType type,
                                                                            ActivityStatus status) {
        return activityJpaRepository.findByUserIdAndTargetIdAndActivityTypeAndStatus(userId, targetId, type, status);
    }

    @Override
    public void changeStatus(Long userId, Long targetId, ActivityType type, ActivityStatus status) {
        activityJpaRepository.updateInvalidStatus(userId, targetId, type, status);
    }
}
