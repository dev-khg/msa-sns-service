package com.example.preorder.follow.core.repository;

import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;

import java.util.Optional;

public interface FollowRepository {
    FollowEntity save(FollowEntity followEntity);

    Optional<FollowEntity> findByFollowById(FollowHistoryKey followHistoryKey);
}
