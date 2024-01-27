package com.example.preorder.follow.core.service;

import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowStatus;
import com.example.preorder.follow.core.event.FollowEventPublisher;
import com.example.preorder.follow.core.event.list.FollowDomainEvent;
import com.example.preorder.follow.core.repositoy.FollowRepository;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.preorder.follow.core.event.list.FollowEvent.*;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowEventPublisher eventPublisher;

    @Transactional
    public void handleFollow(Long followerId, Long followeeId, FollowStatus status) {
        UserEntity followerUser = getUserByUserId(followerId);
        UserEntity followeeUser = getUserByUserId(followeeId);

        FollowEntity followEntity = getFollowEntity(followerId, followeeId);

        if(followEntity == null) {
            followEntity = FollowEntity.create(followerUser, followeeUser, status);
        }

        eventPublisher.followUpdateEvent(followerUser, followeeUser, status);

        followRepository.save(followEntity);
    }

    private FollowEntity getFollowEntity(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).orElse(null);
    }

    private UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not Found User")
        );
    }
}
