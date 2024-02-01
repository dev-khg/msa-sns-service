package hg.activityservice.datasource;

import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import hg.activityservice.core.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ActivityRepositoryImpl implements ActivityRepository {
    private final ActivityJpaRepository activityJpaRepository;

    @Override
    public ActivityEntity save(ActivityEntity entity) {
        return activityJpaRepository.save(entity);
    }

    @Override
    public Optional<ActivityEntity> findById(Long id) {
        return activityJpaRepository.findById(id);
    }

    @Override
    public Optional<ActivityEntity> findByUserIdAndTargetIdAndType(Long userId, Long targetId, ActivityType type) {
        return activityJpaRepository.findByUserIdAndTargetIdAndType(userId, targetId, type);
    }

    @Override
    public List<ActivityEntity> findActivitiesByUserIdIn(List<Long> userId, Pageable pageable) {
        return activityJpaRepository.findActivitiesByUserIdIn(userId, pageable);
    }
}
