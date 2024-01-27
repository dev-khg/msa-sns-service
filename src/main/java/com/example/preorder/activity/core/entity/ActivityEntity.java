package com.example.preorder.activity.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private ActivityType activityType;

    @Column(nullable = false)
    private ActivityStatus status;

    private ActivityEntity(Long userId, Long targetId, ActivityType activityType) {
        this.userId = userId;
        this.targetId = targetId;
        this.activityType = activityType;
        this.status = ActivityStatus.VALID;
    }

    public static ActivityEntity create(Long userId, Long targetId, ActivityType type) {
        return new ActivityEntity(userId, targetId, type);
    }

    public void changeStatus(ActivityStatus status) {
        this.status = status;
    }
}
