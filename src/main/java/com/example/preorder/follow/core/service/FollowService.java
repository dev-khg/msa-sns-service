package com.example.preorder.follow.core.service;

import com.example.preorder.common.activity.ActivityOffer;
import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowStatus;
import com.example.preorder.follow.core.event.FollowEventPublisher;
import com.example.preorder.follow.core.event.list.FollowDomainEvent;
import com.example.preorder.follow.core.repositoy.FollowRepository;
import com.example.preorder.follow.presentation.response.FollowInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.follow.core.event.list.FollowEvent.*;

@Service
@RequiredArgsConstructor
public class FollowService implements ActivityOffer {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowEventPublisher eventPublisher;

    @Transactional
    public void handleFollow(Long followerId, Long followeeId, FollowStatus status) {
        UserEntity followerUser = getUserByUserId(followerId);
        UserEntity followeeUser = getUserByUserId(followeeId);

        FollowEntity followEntity = getFollowEntity(followerId, followeeId);

        if (followEntity == null) {
            followEntity = FollowEntity.create(followerUser, followeeUser, status);
        }

        eventPublisher.followUpdateEvent(followerUser, followeeUser, status);

        followRepository.save(followEntity);
    }

    @Transactional(readOnly = true)
    public List<FollowEntity> getFollowerList(Long userId) {
        return followRepository.followeeList(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponse> handleActivity(List<Long> targetIdList) {
        List<FollowEntity> followEntities = followRepository.findAllByFollowerIdIn(targetIdList);

        return followEntities.stream()
                .map(this::entityToActivityResponse)
                .toList();
    }

    private FollowEntity getFollowEntity(Long followerId, Long followeeId) {
        return followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).orElse(null);
    }

    private UserEntity getUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not Found User")
        );
    }

    private ActivityResponse entityToActivityResponse(FollowEntity followEntity) {
        return new FollowInfoResponse(followEntity);
    }
}
