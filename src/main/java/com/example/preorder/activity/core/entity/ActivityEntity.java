package com.example.preorder.activity.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.common.event.EventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "activity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ActivityKey activityKey;

    private Long publisherId;

    private Long targetId;

    private Integer value;

    @Column(nullable = false)
    private ActivityStatus status;

    public ActivityEntity(ActivityKey activityKey, Long publisherId, Long targetId, Integer value, ActivityStatus status) {
        this.activityKey = activityKey;
        this.publisherId = publisherId;
        this.targetId = targetId;
        this.value = value;
        this.status = status;
    }

    public static ActivityEntity create(ActivityKey key, Long publisherId, Long targetId, Integer value, ActivityStatus status) {
        return new ActivityEntity(key, publisherId, targetId, value, status);
    }
}
