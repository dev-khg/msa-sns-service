package com.example.preorder.comment.presentation;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.comment.core.entity.CommentLikeEntity;
import com.example.preorder.comment.core.entity.CommentLikeStatus;
import com.example.preorder.comment.core.repository.CommentRepository;
import com.example.preorder.comment.presentation.request.CommentCreateRequest;
import com.example.preorder.comment.presentation.request.CommentGetRequest;
import com.example.preorder.comment.presentation.response.CommentActivityResponse;
import com.example.preorder.comment.presentation.response.CommentInfoResponse;
import com.example.preorder.utils.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.comment.core.entity.CommentLikeStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentControllerTest extends IntegrationTest {

    @Test
    @DisplayName("존재하지 않은 포스트에 댓글 작성 시, 예외가 반환되어야 한다.")
    void invalid_comment_enroll() throws Exception {
        // given
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(createRandomUUID());

        // when
        MvcResult mvcResult = mockMvc.perform(post("/comment/" + 100L)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        TestTransaction.end();

        // then


    }

    @Test
    @DisplayName("댓글 작성 시, 디비에 저장되어야 한다.")
    void valid_comment_enroll() throws Exception {
        // given
        Long postId = postEntityList.get(0).getId();
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest(createRandomUUID());

        // when
        MvcResult mvcResult = mockMvc.perform(post("/comment/" + postId)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        String commentId = mvcResult.getResponse().getContentAsString();
        CommentEntity commentEntity = commentRepository.findById(Long.valueOf(commentId)).orElseThrow();

        assertEquals(commentEntity.getUserEntity().getId(), userEntity.getId());
        assertEquals(commentEntity.getContent(), commentCreateRequest.getContent());
        assertEquals(commentEntity.getPostEntity().getId(), postId);
    }

    @Test
    @DisplayName("포스트 댓글 요청시 올바르게 반환되어야 한다.")
    void valid_get_comments_by_post_id() throws Exception {
        // given
        Long postId = postEntityList.get(0).getId();

        // when
        MvcResult mvcResult = mockMvc.perform(get("/comment/" + postId)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        String content = mvcResult.getResponse().getContentAsString();
        CommentInfoResponse[] commentInfoResponses = objectMapper.readValue(content, CommentInfoResponse[].class);

        assertEquals(commentEntityList.size(), commentInfoResponses.length);
    }

    @Test
    @DisplayName("댓글 리스트 요청시 올바르게 반환되어야 한다.")
    void valid_get_comments_by_id_list() throws Exception {
        // given
        List<CommentEntity> commentEntities = commentEntityList;
        List<Long> commentList = commentEntities.stream().map(CommentEntity::getId)
                .toList();

        // when
        MvcResult mvcResult = mockMvc.perform(post("/comment")
                        .content(objectMapper.writeValueAsString(
                                new CommentGetRequest(commentList))
                        )
                        .contentType(APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        String content = mvcResult.getResponse().getContentAsString();
        CommentActivityResponse[] activityResponses = objectMapper.readValue(content, CommentActivityResponse[].class);

        assertEquals(activityResponses.length, commentList.size());
        for (CommentActivityResponse activityResponse : activityResponses) {
            assertNotNull(activityResponse.getPostId());
            assertNotNull(activityResponse.getUsername());
            assertNotNull(activityResponse.getPostUsername());
            assertNotNull(activityResponse.getUserId());
        }
    }

    @Test
    @DisplayName("댓글 좋아요시 디비에 저장되어야 한다.")
    void valid_comment_like() throws Exception {
        // given
        Long commentId = commentEntityList.get(0).getId();

        // when
        mockMvc.perform(post("/comment/" + commentId + "/like")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        CommentLikeEntity commentLikeEntity = commentLikeRepository.findByUserIdAndCommentId(
                userEntity.getId(), commentId
        ).orElseThrow();

        assertNotNull(commentLikeEntity.getId());
        assertEquals(commentLikeEntity.getStatus(), ACTIVATE);
        assertEquals(commentLikeEntity.getUserEntity().getId(), userEntity.getId());
        assertEquals(commentLikeEntity.getCommentEntity().getId(), commentId);
    }

    @Test
    @DisplayName("댓글 좋아요 취소시 디비에 저장되어야 한다.")
    void valid_comment_unlike() throws Exception {
        // given
        Long commentId = commentEntityList.get(0).getId();

        // when
        mockMvc.perform(delete("/comment/" + commentId + "/like")
                        .contentType(APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // then
        CommentLikeEntity commentLikeEntity = commentLikeRepository.findByUserIdAndCommentId(
                userEntity.getId(), commentId
        ).orElseThrow();

        assertNotNull(commentLikeEntity.getId());
        assertEquals(commentLikeEntity.getStatus(), DEACTIVATE);
        assertEquals(commentLikeEntity.getUserEntity().getId(), userEntity.getId());
        assertEquals(commentLikeEntity.getCommentEntity().getId(), commentId);
    }
}