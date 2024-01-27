package com.example.preorder.follow.core.entity;

import com.example.preorder.common.entity.BaseTimeEntity;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.user.core.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FollowEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "follower_id", nullable = false)
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "followee_id", nullable = false)
    private UserEntity followee;

    @Column(nullable = false)
    private FollowStatus status;

    private FollowEntity(UserEntity follower, UserEntity followee, FollowStatus status) {
        this.follower = follower;
        this.followee = followee;
        this.status = status;
    }

    public static FollowEntity create(UserEntity follower, UserEntity followee, FollowStatus status) {
        if (follower == followee) {
            throw new BadRequestException("can not follow yourself.");
        }

        return new FollowEntity(follower, followee, status);
    }

    public void changeStatus(FollowStatus status) {
        this.status = status;
    }
}
