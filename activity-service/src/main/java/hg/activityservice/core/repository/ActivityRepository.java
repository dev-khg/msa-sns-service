package hg.activityservice.core.repository;

import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    ActivityEntity save(ActivityEntity entity);

    Optional<ActivityEntity> findById(Long id);

    Optional<ActivityEntity> findByUserIdAndTargetIdAndType(Long userId, Long targetId, ActivityType type);

    List<ActivityEntity> findActivitiesByUserIdIn(List<Long> userId,Pageable pageable);
}
