package com.example.preorder.follow.db;

import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, FollowHistoryKey> {
}
