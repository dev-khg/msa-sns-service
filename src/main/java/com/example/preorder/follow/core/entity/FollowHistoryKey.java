package com.example.preorder.follow.core.entity;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FollowHistoryKey implements Serializable {
    @Column(nullable = false)
    private Long followerId;

    @Column(nullable = false)
    private Long followeeId;

    public static FollowHistoryKey create(Long followerId, Long followeeId) {
        return new FollowHistoryKey(followerId, followeeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FollowHistoryKey that = (FollowHistoryKey) o;
        return Objects.equals(followerId, that.followerId) && Objects.equals(followeeId, that.followeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followeeId);
    }
}
