package com.example.newsfeedservice.presentation;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import com.example.newsfeedservice.presentation.request.ActivityRequest;
import com.example.newsfeedservice.presentation.request.CommentCreateRequest;
import com.example.newsfeedservice.testconfiguration.IntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Disabled
class CommentControllerTest extends IntegrationTest {

    @Test
    @DisplayName("댓글 정상적으로 등록시, 디비에 저장되어야 한다.")
    void success_enroll_comment() throws Exception {
        // given
        PostEntity postEntity = postEntities.get(0);
        Long userId = 1L;
        String content = createRandomUUID();
        CommentCreateRequest request = new CommentCreateRequest(content);

        // when
        MvcResult mvcResult = mockMvc.perform(post("/comment/" + postEntity.getId())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                        .header("user-id", userId)
                ).andExpect(status().isOk())
                .andReturn();
        flushAndClearPersistence();
        // then
        Long commentId = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), Long.class);

        assertTrue(commentRepository.findById(commentId).isPresent());
    }

    @Test
    @DisplayName("존재하지 않는 포스트에 댓글 정상적으로 등록시, Bad Request가 반환되어야 한다.")
    void failure_enroll_comment_to_not_exists_post() throws Exception {
        // given
        PostEntity postEntity = postEntities.get(0);
        Long userId = 1L;
        String content = createRandomUUID();
        CommentCreateRequest request = new CommentCreateRequest(content);

        // when
        mockMvc.perform(post("/comment/" + Long.MAX_VALUE)
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header("user-id", userId)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 좋아요 요청시 Bad Request가 반환되어야 한다.")
    void failure_comment_like_not_exists_comment() throws Exception {
        // given
        Long userId = 1L;

        // when
        mockMvc.perform(post("/comment/" + Long.MAX_VALUE + "/like")
                .header("user-id", userId)
        ).andExpect(status().isBadRequest());

        // then
    }

    @Test
    @DisplayName("존재하는 댓글에 좋아요 요청시 디비에 저장되어야 한다.")
    void success_comment_like() throws Exception {
        // given
        Long userId = 1L;
        PostEntity postEntity = postEntities.get(0);
        Long commentId =
                commentRepository.save(CommentEntity.create(userId, postEntity, createRandomUUID())).getId();
        flushAndClearPersistence();

        // when
        mockMvc.perform(post("/comment/" + commentId + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        Optional<CommentLikeEntity> optionalCommentLike
                = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        assertTrue(optionalCommentLike.isPresent());
    }

    @Test
    @DisplayName("좋아요를 하지 않는 댓글에 좋아요 취소 요청시 디비에 저장되면 안된다.")
    void success_comment_unlike_not_exists_comment() throws Exception {
        // given
        Long userId = 1L;
        PostEntity postEntity = postEntities.get(0);
        Long commentId =
                commentRepository.save(CommentEntity.create(userId, postEntity, createRandomUUID())).getId();
        flushAndClearPersistence();

        // when
        mockMvc.perform(delete("/comment/" + commentId + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        Optional<CommentLikeEntity> optionalCommentLike
                = commentLikeRepository.findByUserIdAndCommentId(userId, commentId);

        assertTrue(optionalCommentLike.isEmpty());
    }

    @Test
    @DisplayName("댓글 좋아요 취소시 정상적으로 반영되어야 한다.")
    void success_comment_unlike() throws Exception {
        // given
        Long userId = 1L;
        PostEntity postEntity = postEntities.get(0);
        CommentEntity comment
                = commentRepository.save(CommentEntity.create(userId, postEntity, createRandomUUID()));
        CommentLikeEntity commentLikeEntity = CommentLikeEntity.create(userId, comment);
        commentLikeRepository.save(commentLikeEntity);
        flushAndClearPersistence();

        // when
        mockMvc.perform(delete("/comment/" + comment.getId() + "/like")
                .header("user-id", userId)
        ).andExpect(status().isNoContent());
        flushAndClearPersistence();

        // then
        CommentLikeEntity savedEntity = commentLikeRepository.findByUserIdAndCommentId(userId, comment.getId())
                .orElseThrow();

        assertNotNull(savedEntity.getDeletedAt());
    }

    @Test
    @DisplayName("댓글 내역은 올바르게 조회되어야 한다.")
    void success_get_comment_activities() throws Exception {
        // given
        Long userId = 1L;
        PostEntity postEntity = postEntities.get(0);
        Map<Long, CommentEntity> commentEntityMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            CommentEntity save = commentRepository.save(CommentEntity.create(userId, postEntity, createRandomUUID()));
            commentEntityMap.put(save.getId(), save);
        }
        flushAndClearPersistence();

        // when
        List<Long> commentIdList
                = new java.util.ArrayList<>(commentEntityMap.keySet().stream().toList());

        // then
        while (!commentIdList.isEmpty()) {
            ActivityRequest value = new ActivityRequest(commentIdList);
            MvcResult mvcResult = mockMvc.perform(post("/comment/activity")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(value))
                    ).andExpect(status().isOk())
                    .andReturn();

            CommentActivityDTO[] commentActivityDTOS
                    = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), CommentActivityDTO[].class);

            assertEquals(commentActivityDTOS.length, commentIdList.size());
            commentIdList.remove(0);
            flushAndClearPersistence();
        }
    }

    @Test
    @DisplayName("댓글 좋아요 내역은 올바르게 조회되어야 한다.")
    void success_get_comment_like_activities() throws Exception {
        // given
        Long userId = 1L;
        PostEntity postEntity = postEntities.get(0);
        CommentEntity comment
                = commentRepository.save(CommentEntity.create(userId, postEntity, createRandomUUID()));
        Map<Long, CommentLikeEntity> commentLikeEntityMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            CommentLikeEntity save = commentLikeRepository.save(CommentLikeEntity.create(userId + 1, comment));
            commentLikeEntityMap.put(save.getId(), save);
        }
        flushAndClearPersistence();

        // when
        List<Long> commentLikeIdList
                = new java.util.ArrayList<>(commentLikeEntityMap.keySet().stream().toList());

        // then
        while (!commentLikeIdList.isEmpty()) {
            ActivityRequest value = new ActivityRequest(commentLikeIdList);
            MvcResult mvcResult = mockMvc.perform(post("/comment/like/activity")
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(value))
                    ).andExpect(status().isOk())
                    .andReturn();

            CommentLikeActivityDTO[] commentLikeActivityDTOS
                    = readResponseJsonBody(mvcResult.getResponse().getContentAsString(), CommentLikeActivityDTO[].class);

            assertEquals(commentLikeActivityDTOS.length, commentLikeIdList.size());
            commentLikeIdList.remove(0);
            flushAndClearPersistence();
        }
    }
}