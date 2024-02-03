package hg.userservice.presentation;

import hg.userservice.core.entity.FollowEntity;
import hg.userservice.core.entity.UserEntity;
import hg.userservice.testconfiguration.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class FollowControllerTest extends IntegrationTest {

    @Test
    @DisplayName("존재하지않는 유저 팔로우 요청시 Bad Request가 발생해야 한다.")
    void failure_follow_request() throws Exception {
        // given
        UserEntity follower = saveUserList.get(0);

        // when
        mockMvc.perform(post("/follow/" + Long.MAX_VALUE)
                .header("user-id", follower.getId())
        ).andExpect(status().isBadRequest());
    }

//    @Test
    @DisplayName("팔로우 요청시 올바르게 반영되어야 한다.")
    void success_follow_request() throws Exception {
        // given
        UserEntity follower = saveUserList.get(0);
        UserEntity followee = saveUserList.get(1);

        // when
        mockMvc.perform(post("/follow/" + followee.getId())
                .header("user-id", follower.getId())
        ).andExpect(status().isNoContent());

        // then
        Optional<FollowEntity> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(follower.getId(), followee.getId());
        assertTrue(optionalFollow.isPresent());
    }

    @Test
    @DisplayName("존재하지않는 유저 언팔로우 요청시 Bad Request가 발생해야 한다.")
    void failure_un_follow_request() throws Exception {
        // given
        UserEntity follower = saveUserList.get(0);

        // when
        mockMvc.perform(delete("/follow/" + Long.MAX_VALUE)
                .header("user-id", follower.getId())
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하는 유저 언팔로우 요청시 정상 처리되어야 한다.")
    void success_un_follow_request() throws Exception {
        // given
        UserEntity follower = saveUserList.get(0);
        UserEntity followee = saveUserList.get(1);

        // when
        mockMvc.perform(delete("/follow/" + followee.getId())
                .header("user-id", follower.getId())
        ).andExpect(status().isNoContent());
    }
}