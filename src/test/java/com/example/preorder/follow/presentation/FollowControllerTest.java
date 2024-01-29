package com.example.preorder.follow.presentation;

import com.example.preorder.follow.core.entity.FollowEntity;
import com.example.preorder.follow.core.repositoy.FollowRepository;
import com.example.preorder.utils.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;

import static com.example.preorder.follow.core.entity.FollowStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
class FollowControllerTest extends IntegrationTest {
    @Autowired
    FollowRepository followRepository;

    @Test
    @DisplayName("존재하지 않는 유저 팔로우 요청시 예외 발생해야한다.")
    void follow_not_exists_user() throws Exception {
        // given

        // when
        mockMvc.perform(post("/follow/" + 100L)
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        // then
    }

    @Test
    @DisplayName("존재하는 유저 팔로우시 데이터베이스에 저장되어야 한다.")
    void valid_follow_user() throws Exception {
        // given

        // when
        mockMvc.perform(post("/follow/" + userEntityA.getId())
                .header(AUTHORIZATION, accessToken)
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isNoContent());

        flushAndClearPersistence();

        // then
        FollowEntity followEntity = followRepository.findByFollowerIdAndFolloweeId(
                userEntity.getId(), userEntityA.getId()
        ).orElseThrow();

        assertEquals(followEntity.getFollower().getId(), userEntity.getId());
        assertEquals(followEntity.getFollowee().getId(), userEntityA.getId());
        assertEquals(followEntity.getStatus(), FOLLOWING);
    }
}