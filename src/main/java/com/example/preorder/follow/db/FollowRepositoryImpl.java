package com.example.preorder.follow.db;

import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.repositoy.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {
    private final FollowJpaRepository followJpaRepository;

    @Override
    public FollowEntity save(FollowEntity followEntity) {
        return followJpaRepository.save(followEntity);
    }

    @Override
    public Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public List<FollowEntity> followeeList(Long userId) {
        return followJpaRepository.findAllByFollowerId(userId);
    }

    @Override
    public List<FollowEntity> findAllByFollowerIdIn(List<Long> followerId) {
        return followJpaRepository.findAllByFollowerIdIn(followerId);
    }
}
