package com.example.preorder.follow.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.follow.core.vo.FollowStatus;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FollowEntity extends BaseTimeEntity {
    @EmbeddedId
    private FollowHistoryKey followHistoryKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FollowStatus status;

    public static FollowEntity create(Long followerId, Long followeeId) {
        return new FollowEntity(FollowHistoryKey.create(followerId, followeeId), FollowStatus.ACTIVE);
    }

    public void changeStatus(FollowStatus status) {
        this.status = status;
    }
}
