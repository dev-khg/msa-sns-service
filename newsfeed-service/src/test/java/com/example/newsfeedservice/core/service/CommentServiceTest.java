package com.example.newsfeedservice.core.service;

import com.example.commonproject.event.EventPublisher;
import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import com.example.newsfeedservice.datasource.CommentRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.newsfeedservice.core.entity.CommentEntity.create;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentServiceTest {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    PostRepository postRepository;
    @MockBean
    ActivityFeignClient activityFeignClient;
    @MockBean
    EventPublisher eventPublisher;
    @PersistenceContext
    EntityManager em;

    PostEntity postEntity;

    @BeforeEach
    void beforeEach() {
        postEntity = PostEntity.create(1L, createRandomUUID());
        postRepository.save(postEntity);
        flushAndClearPersistence();
    }

    @Test
    @DisplayName("댓글 저장 요청시, 디비에서 조회가 가능해야 한다.")
    void save_comment() {
        // given
        Long userId = 2L;
        String content = createRandomUUID();

        // when
        Long savedId = commentService.enrollComment(userId, postEntity.getId(), content);
        flushAndClearPersistence();

        // then
        CommentEntity savedComment = commentRepository.findById(savedId).orElseThrow();

        assertNotNull(savedComment.getId());
        assertEquals(savedComment.getPostEntity().getId(), postEntity.getId());
        assertEquals(savedComment.getContent(), content);
        assertNotNull(savedComment.getCreatedAt());
        assertNotNull(savedComment.getUpdatedAt());
        assertNull(savedComment.getDeletedAt());
    }

    @Test
    @DisplayName("저장된 댓글 아이디 리스트는 올바르게 조회되어야 한다.")
    void find_post_like_list() {
        // given
        Long userId = 1L;
        Map<Long, CommentEntity> commentEntityMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            CommentEntity save = commentRepository.save(create(userId, postEntity, createRandomUUID()));
            commentEntityMap.put(save.getId(), save);
        }
        flushAndClearPersistence();

        // when

        // then
        List<Long> commentIdList = new ArrayList<>(commentEntityMap.keySet().stream().toList());
        while (!commentIdList.isEmpty()) {
            List<CommentActivityDTO> commentActivities = commentService.getCommentActivities(commentIdList);
            assertEquals(commentActivities.size(), commentIdList.size());
            commentIdList.remove(0);
            flushAndClearPersistence();
        }
    }

    private void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    private String createRandomUUID() {
        return UUID.randomUUID().toString();
    }
}