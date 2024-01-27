package com.example.preorder.post.presentation;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.repository.PostRepository;
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
class PostControllerTest extends IntegrationTest {

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("게시글 등록 시, 정상적으로 디비에 저장되어야 한다.")
    void valid_create_post() throws Exception {
        // given
        PostCreateRequest postCreateRequest = new PostCreateRequest(createRandomUUID());

        // when
        MvcResult mvcResult = mockMvc.perform(post("/post")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequest))
                        .header(AUTHORIZATION, accessToken)
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

    @Test
    @DisplayName("아이디로 조회시, 저장된 포스트들이 반환되어야 한다.")
    void valid_find_all_post_by_id() throws Exception {
        // given
        List<Long> postIds = this.postEntityList.stream()
                .map(PostEntity::getId)
                .toList();
        PostGetRequest postGetRequest = new PostGetRequest(postIds);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/post/all")
                        .content(objectMapper.writeValueAsString(postGetRequest))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        String contentAsString = mvcResult.getResponse().getContentAsString();
        PostInfoResponse[] postInfoResponses = objectMapper.readValue(contentAsString, PostInfoResponse[].class);

        for (PostInfoResponse postInfoResponse : postInfoResponses) {
            assertNotNull(postInfoResponse.getPostId());
            assertNotNull(postInfoResponse.getUsername());
            assertNotNull(postInfoResponse.getUserId());
        }
    }
}