package com.example.preorder.activity.core.repository;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.activity.core.entity.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface ActivityJpaRepository extends JpaRepository<ActivityEntity, Long> {

    @Modifying
    @Query("update ActivityEntity a " +
            "set a.status = :status " +
            "where a.targetId = :targetId and a.activityType = :type and a.userId = :userId")
    void updateInvalidStatus(@Param("targetId") Long targetId,
                             @Param("userId") Long userId,
                             @Param("type") ActivityType activityType,
                             @Param("status") ActivityStatus status);

    // TODO : 수정 필요
//    @Query("select a from ActivityEntity a " +
//            "where a.status = :status and a.userId in (:user_id_list)")
//    List<ActivityEntity> findActivitiesByUserId(@Param("user_id_list") List<Long> userIdList,
//                                                @Param("status") ActivityStatus status,
//                                                Pageable pageable);

    Optional<ActivityEntity> findByUserIdAndTargetIdAndActivityTypeAndStatus(Long userId,
                                                                             Long targetId,
                                                                             ActivityType type,
                                                                             ActivityStatus status);
}
