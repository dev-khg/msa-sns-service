package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.newsfeedservice.core.entity.CommentEntity.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentRepositoryImplTest {
    @Autowired
    CommentRepositoryImpl commentRepository;
    @Autowired
    PostRepository postRepository;

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
    @DisplayName("댓글 저장 시, 디비에 반영되어야 한다.")
    void save_comment() {
        // given
        Long userId = 2L;
        String content = createRandomUUID();
        CommentEntity commentEntity = create(1L, postEntity, content);

        // when
        Long savedId = commentRepository.save(commentEntity).getId();
        flushAndClearPersistence();

        // then
        CommentEntity savedComment = commentRepository.findById(savedId).orElseThrow();

        assertNotNull(savedComment.getId());
        assertEquals(commentEntity.getPostEntity().getId(), postEntity.getId());
        assertEquals(commentEntity.getContent(), content);
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
            List<CommentActivityDTO> commentActivities = commentRepository.findCommentByIdList(commentIdList);
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