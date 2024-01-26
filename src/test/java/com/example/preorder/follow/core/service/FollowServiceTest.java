package com.example.preorder.follow.core.service;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;
import com.example.preorder.follow.core.repository.FollowRepository;
import com.example.preorder.follow.core.vo.FollowStatus;
import com.example.preorder.utils.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

import static com.example.preorder.follow.core.entity.FollowHistoryKey.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class FollowServiceTest extends IntegrationTest {
    @Autowired
    FollowService followService;
    @Autowired
    FollowRepository followRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @ParameterizedTest
    @DisplayName("존재하지 않는 유저 팔로우시 예외가 반환되어야 한다.")
    @EnumSource(FollowStatus.class)
    void follow_not_exists_user_throws_exception(FollowStatus status) {
        // given

        // when

        // then
        assertThatThrownBy(
                () -> followService.handleFollow(
                        otherUserEntityA.getId(),
                        100L,
                        status
                )
        ).isInstanceOf(BadRequestException.class);
    }

    @ParameterizedTest
    @DisplayName("존재하는 유저 팔로우시 정상적으로 저장되어야 한다.")
    @EnumSource(FollowStatus.class)
    void follow_exists_user(FollowStatus status) {
        // given

        // when
        followService.handleFollow(otherUserEntityA.getId(), otherUserEntityB.getId(), status);

        // then
        Optional<FollowEntity> optionalFollow = followRepository.findByFollowById(
                create(
                        otherUserEntityA.getId(),
                        otherUserEntityB.getId())
        );
        assertTrue(optionalFollow.isPresent());
        assertEquals(optionalFollow.get().getStatus(), status);
        assertEquals(
                optionalFollow.get().getFollowHistoryKey().getFollowerId(),
                otherUserEntityA.getId()
        );
        assertEquals(
                optionalFollow.get().getFollowHistoryKey().getFolloweeId(),
                otherUserEntityB.getId()
        );
    }
}