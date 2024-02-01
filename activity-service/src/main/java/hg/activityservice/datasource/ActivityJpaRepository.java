package hg.activityservice.datasource;

import com.example.commonproject.activity.ActivityType;
import hg.activityservice.core.entity.ActivityEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityJpaRepository extends JpaRepository<ActivityEntity, Long> {
    Optional<ActivityEntity> findByUserIdAndTargetIdAndType(Long userId, Long targetId, ActivityType type);

    @Query("select a from ActivityEntity a " +
            "where a.userId in (:user_id_list) " +
            "and a.deletedAt is null")
    List<ActivityEntity> findActivitiesByUserIdIn(@Param("user_id_list") List<Long> userIdList, Pageable pageable);
}
