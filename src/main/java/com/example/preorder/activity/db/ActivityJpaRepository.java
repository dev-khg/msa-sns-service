package com.example.preorder.activity.db;

import com.example.preorder.activity.core.entity.ActivityEntity;
import com.example.preorder.activity.core.entity.ActivityKey;
import com.example.preorder.activity.core.entity.ActivityStatus;
import com.example.preorder.common.event.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityJpaRepository extends JpaRepository<ActivityEntity, Long> {
    @Query("select ac from ActivityEntity ac " +
            "where ac.status = :status and " +
            "ac.activityKey.eventType = :type and " +
            "ac.targetId = :targetId and " +
            "ac.activityKey.eventOrdinal = :ordinal" +
            "")
    List<ActivityEntity> findByConditions(@Param("type") EventType type,
                                          @Param("ordinal") Integer ordinal,
                                          @Param("targetId") Long targetId,
                                          @Param("status") ActivityStatus status
    );
}
