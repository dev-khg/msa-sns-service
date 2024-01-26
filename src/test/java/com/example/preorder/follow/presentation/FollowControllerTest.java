package com.example.preorder.follow.presentation;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.entity.FollowHistoryKey;
import com.example.preorder.follow.core.repository.FollowRepository;
import com.example.preorder.follow.core.service.FollowService;
import com.example.preorder.follow.core.vo.FollowStatus;
import com.example.preorder.utils.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.preorder.follow.core.entity.FollowHistoryKey.create;
import static com.example.preorder.follow.core.vo.FollowStatus.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class FollowControllerTest extends IntegrationTest {
    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("follow 요청시 데이터가 저장되어야 한다.")
    void request_follow() throws Exception{
        // given

        // when
        mockMvc.perform(
                        post("/follow/" + otherUserEntityA.getId())
                                .header(AUTHORIZATION, accessToken)
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        Optional<FollowEntity> optionalFollow = followRepository.findByFollowById(
                create(userEntity.getId(), otherUserEntityA.getId())
        );

        assertTrue(optionalFollow.isPresent());
        assertEquals(optionalFollow.get().getStatus(), ACTIVE);
        assertEquals(optionalFollow.get().getFollowHistoryKey().getFollowerId(), userEntity.getId());
        assertEquals(optionalFollow.get().getFollowHistoryKey().getFolloweeId(), otherUserEntityA.getId());
    }

    @Test
    @DisplayName("follow 해제시 데이터가 저장되어야 한다.")
    void request_unfollow() throws Exception{
        // given

        // when
        mockMvc.perform(
                        delete("/follow/" + otherUserEntityA.getId())
                                .header(AUTHORIZATION, accessToken)
                ).andExpect(status().isNoContent())
                .andReturn();

        // then
        Optional<FollowEntity> optionalFollow = followRepository.findByFollowById(
                create(userEntity.getId(), otherUserEntityA.getId())
        );

        assertTrue(optionalFollow.isPresent());
        assertEquals(optionalFollow.get().getStatus(), DEACTIVATE);
        assertEquals(optionalFollow.get().getFollowHistoryKey().getFollowerId(), userEntity.getId());
        assertEquals(optionalFollow.get().getFollowHistoryKey().getFolloweeId(), otherUserEntityA.getId());
    }

}