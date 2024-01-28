package com.example.preorder.follow.core.repositoy;

import com.example.preorder.follow.core.entity.FollowEntity;

import java.util.List;
import java.util.Optional;

public interface FollowRepository {
    FollowEntity save(FollowEntity followEntity);

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    List<FollowEntity> followeeList(Long userId);

    List<FollowEntity> findAllByFollowerIdIn(List<Long> followerId);
}
