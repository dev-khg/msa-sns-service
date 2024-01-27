package com.example.preorder.follow.db;

import com.example.preorder.follow.core.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
