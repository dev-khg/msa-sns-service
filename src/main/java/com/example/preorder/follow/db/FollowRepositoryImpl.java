package com.example.preorder.follow.db;

import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;
import com.example.preorder.follow.core.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public Optional<FollowEntity> findByFollowById(FollowHistoryKey followHistoryKey) {
        return followJpaRepository.findById(followHistoryKey);
    }
}
