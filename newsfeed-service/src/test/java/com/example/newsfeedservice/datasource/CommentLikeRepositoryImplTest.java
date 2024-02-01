package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.CommentLikeRepository;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.newsfeedservice.core.entity.CommentEntity.create;
import static com.example.newsfeedservice.core.entity.CommentLikeEntity.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentLikeRepositoryImplTest {
    @Autowired
    CommentLikeRepositoryImpl commentLikeRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    PostRepository postRepository;

    @PersistenceContext
    EntityManager em;

    PostEntity postEntity;

    CommentEntity commentEntity;

    @BeforeEach
    void beforeEach() {
        postEntity = postRepository.save(PostEntity.create(1L, createRandomUUID()));
        commentEntity = commentRepository.save(CommentEntity.create(1L, postEntity, createRandomUUID()));
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("댓글 좋아요 저장시 디비에서 조회가 가능해야 한다.")
    void save_comment_like() {
        // given
        Long userId = 1L;
        Long commentId = commentEntity.getId();
        CommentLikeEntity likeEntity = CommentLikeEntity.create(1L, commentEntity);

        // when
        commentLikeRepository.save(likeEntity);
        flushAndClearPersistence();

        // then
        CommentLikeEntity savedEntity = commentLikeRepository.findByUserIdAndCommentId(userId, commentId).orElseThrow();

        assertNotNull(savedEntity.getId());
        assertNotNull(savedEntity.getCreatedAt());
        assertNotNull(savedEntity.getUpdatedAt());
        assertEquals(savedEntity.getCommentEntity().getId(), commentId);
        assertEquals(savedEntity.getUserId(), userId);
        assertNull(savedEntity.getDeletedAt());
    }

    @Test
    @DisplayName("댓글 좋아요 리스트 조회시 정상적으로 조회되어야 한다.")
    void save_duplicate_user_id_and_comment_id() {
        // given
        Long userId = 1L;
        Map<Long, CommentLikeEntity> commentLikeEntityMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            CommentLikeEntity save = commentLikeRepository.save(CommentLikeEntity.create(userId, commentEntity));
            commentLikeEntityMap.put(save.getId(), save);
        }
        flushAndClearPersistence();

        // when

        // then
        List<Long> commentLikeIdList = new ArrayList<>(commentLikeEntityMap.keySet().stream().toList());
        while (!commentLikeIdList.isEmpty()) {
            List<CommentLikeActivityDTO> commentLikeActivityDTOS = commentLikeRepository.findByIdList(commentLikeIdList);
            assertEquals(commentLikeActivityDTOS.size(), commentLikeIdList.size());
            commentLikeIdList.remove(0);
            flushAndClearPersistence();
        }
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private PostEntity createPostEntity(Long userId, String content) {
        return PostEntity.create(userId, content);
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}