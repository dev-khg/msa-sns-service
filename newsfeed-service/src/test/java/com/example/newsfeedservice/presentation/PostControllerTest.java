package com.example.newsfeedservice.presentation;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.presentation.request.PostCreateRequest;
import com.example.newsfeedservice.testconfiguration.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
class PostControllerTest extends IntegrationTest {

    @Test
    void success_enroll_post() throws Exception {
        // given
        Long userId = 1L;
        String content = createRandomUUID();
        PostCreateRequest request = new PostCreateRequest(content);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/post")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .header("user-id", userId)
                ).andExpect(status().isOk())
                .andReturn();

        // then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Long data = readResponseJsonBody(contentAsString, Long.class);
        PostEntity postEntity = postRepository.findById(data).orElseThrow();

        assertNotNull(postEntity.getId());
        assertNotNull(postEntity.getUpdatedAt());
        assertNotNull(postEntity.getCreatedAt());
        assertEquals(postEntity.getContent(), content);
        assertNull(postEntity.getDeletedAt());
    }

    @Test
    @DisplayName("헤더에 유저아이디가 올바르지 않으면, 권한 없는 요청이 반환된다.")
    void failure_authorization() throws Exception {
        // given
        String content = createRandomUUID();
        PostCreateRequest request = new PostCreateRequest(content);

        // when
        mockMvc.perform(post("/post")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());

        mockMvc.perform(post("/post")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header("user-id", "")
        ).andExpect(status().isUnauthorized());

        mockMvc.perform(post("/post")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header("user-id", "a1")
        ).andExpect(status().isUnauthorized());

        mockMvc.perform(post("/post")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header("user-id", " ")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 포스트 좋아요 요청시 BadRequest가 응답된다.")
    void failure_post_like_not_exists_post() throws Exception {
        // given

        // when
        mockMvc.perform(post("/post/" + Long.MAX_VALUE + "/like")
                .header("user-id", 1L)
        ).andExpect(status().isBadRequest());

        // then
    }

    @Test
    @DisplayName("존재하는 포스트 좋아요 요청시 데이터베이스에 저장되어야한다.")
    void success_post_like_exists_post() throws Exception {
        // given
        PostEntity postEntity = postEntities.get(0);
        Long userId = 1L;

        // when
        mockMvc.perform(post("/post/" + postEntity.getId() + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        PostLikeEntity savedEntity = postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId()).orElseThrow();

        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getUserId(), userId);
        assertEquals(savedEntity.getPostEntity().getId(), postEntity.getId());
        assertNotNull(savedEntity.getUpdatedAt());
        assertNotNull(savedEntity.getCreatedAt());
        assertNull(savedEntity.getDeletedAt());
    }

    @Test
    @DisplayName("존재하지 않는 포스트 좋아요 취소 요청시 데이터베이스에 저장되면 안된다.")
    void success_post_unlike_not_exists_post_like() throws Exception {
        // given
        PostEntity postEntity = postEntities.get(0);
        Long userId = 1L;

        // when
        mockMvc.perform(delete("/post/" + postEntity.getId() + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then

        Optional<PostLikeEntity> optionalPostLike = postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId());
        assertTrue(optionalPostLike.isEmpty());
    }

    @Test
    @DisplayName("존재하는 포스트 좋아요 취소 요청시 데이터베이스에 반환되어야 한다.")
    void success_post_unlike_exists_post_like() throws Exception {
        // given
        PostEntity postEntity = postEntities.get(0);
        Long userId = 1L;
        PostLikeEntity save = postLikeRepository.save(PostLikeEntity.create(userId, postEntity));
        flushAndClearPersistence();

        // when
        mockMvc.perform(delete("/post/" + postEntity.getId() + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then

        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId()).orElseThrow();

        assertNotNull(postLikeEntity.getDeletedAt());
        assertNotEquals(save.getUpdatedAt(), postLikeEntity.getUpdatedAt());
    }
}