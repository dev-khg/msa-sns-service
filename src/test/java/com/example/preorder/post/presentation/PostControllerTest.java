package com.example.preorder.post.presentation;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.repository.PostLikeRepository;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.db.PostLikeJpaRepository;
import com.example.preorder.post.presentation.request.PostCreateRequest;
import com.example.preorder.post.presentation.request.PostGetRequest;
import com.example.preorder.post.presentation.response.PostInfoResponse;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.utils.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostControllerTest extends IntegrationTest {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostLikeRepository postLikeRepository;

    @Test
    @DisplayName("게시글 등록 시, 정상적으로 디비에 저장되어야 한다.")
    void valid_create_post() throws Exception {
        // given
        PostCreateRequest postCreateRequest = new PostCreateRequest(createRandomUUID());

        // when
        MvcResult mvcResult = mockMvc.perform(post("/post")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        long savedPostId = Long.parseLong(mvcResult.getResponse().getContentAsString());
        PostEntity postEntity = postRepository.findById(savedPostId).orElseThrow();

        assertEquals(postEntity.getUserEntity().getId(), userEntity.getId());
        assertEquals(postEntity.getContent(), postCreateRequest.getContent());
        assertNotNull(postEntity.getCreatedAt());
        assertNull(postEntity.getDeletedAt());
    }

    @Autowired
    PostLikeJpaRepository temp;

    @Test
    @DisplayName("포스트 좋아요 요청시 디비에 저장되어야 한다.")
    void valid_post_like() throws Exception {
        // given
        Long postId = postEntityList.get(0).getId();

        // when
        mockMvc.perform(post("/post/" + postId + "/like")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        flushAndClearPersistence();
        // then
        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(
                userEntity.getId(), postId
        ).orElseThrow();

        assertNotNull(postLikeEntity.getId());
        assertNotNull(postLikeEntity.getCreatedAt());
        assertNull(postLikeEntity.getDeletedAt());
        assertEquals(postLikeEntity.getPostEntity().getId(), postId);
        assertEquals(postLikeEntity.getUserEntity().getId(), userEntity.getId());
    }

    @Test
    @DisplayName("포스트 좋아요 취소시 요청시 디비에 조회되면 안된다.")
    void valid_post_unlike() throws Exception {
        // given
        Long postId = postEntityList.get(0).getId();

        // when
        mockMvc.perform(delete("/post/" + postId + "/like")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        flushAndClearPersistence();
        // then

        assertTrue(postLikeRepository.findByUserIdAndPostId(
                userEntity.getId(), postId
        ).isEmpty());
    }
}