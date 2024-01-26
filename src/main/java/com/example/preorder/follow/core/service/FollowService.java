package com.example.preorder.follow.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;
import com.example.preorder.follow.core.event.FollowDomainEvent;
import com.example.preorder.follow.core.repository.FollowRepository;
import com.example.preorder.follow.core.vo.FollowStatus;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public void handleFollow(Long followerId, Long followeeId, FollowStatus status) {
        if (userRepository.findById(followeeId).isEmpty()) {
            throw new BadRequestException("followee is not exists");
        }

        FollowEntity followEntity = null;

        FollowHistoryKey followHistoryKey = FollowHistoryKey.create(followerId, followeeId);
        followEntity = followRepository.findByFollowById(followHistoryKey).orElse(null);

        if (followEntity == null) {
            followEntity = FollowEntity.create(followerId, followeeId);
        }

        followEntity.changeStatus(status);

        followRepository.save(followEntity);
        eventPublisher.publishEvent(new FollowDomainEvent(followerId, followeeId, status));
    }
}
