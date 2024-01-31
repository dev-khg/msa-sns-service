package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostRepositoryImplTest {
    @Autowired
    PostRepositoryImpl postRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("엔티티 저장시 값이 올바르게 설정되어야 한다.")
    void save_entity_validation() {
        // given
        Long userId = 1L;
        String content = createRandomUUID();
        PostEntity postEntity = createPostEntity(userId, content);

        // when
        Long postId = postRepository.save(postEntity).getId();
        flushAndClearPersistence();

        // then
        PostEntity savedPost = postRepository.findById(postId).orElseThrow();
        assertNotNull(savedPost.getId());
        assertNotNull(savedPost.getUserId());
        assertEquals(savedPost.getContent(), content);
        assertNull(savedPost.getDeletedAt());
        assertNotNull(savedPost.getCreatedAt());
        assertNotNull(savedPost.getUpdatedAt());
    }

    @Test
    @DisplayName("아이디 리스트로 조회시 올바르게 반환되어야 한다.")
    void find_by_id_list() {
        // given
        Map<Long, PostEntity> postInputMap = new HashMap<>();
        Long userId = 1L;
        for (int i = 0; i < 10; i++) {
            PostEntity postEntity = createPostEntity(userId, createRandomUUID());
            postRepository.save(postEntity);
            postInputMap.put(postEntity.getId(), postEntity);
        }
        flushAndClearPersistence();

        // when

        // then
        List<Long> postIdList = new ArrayList<>(postInputMap.keySet().stream().toList());

        while (!postIdList.isEmpty()) { // 하나씩 아이디를 지워가면서 조회하고, 결과 유효성 판단
            List<PostActivityDTO> activityDTOS = postRepository.findPostIdList(postIdList);
            for (PostActivityDTO activityDTO : activityDTOS) {
                PostEntity postEntity = postInputMap.get(activityDTO.getPostId());

                assertEquals(postEntity.getUserId(), activityDTO.getUserId());
                assertEquals(postEntity.getContent(), activityDTO.getContent());
                assertEquals(postEntity.getCreatedAt(), activityDTO.getCreatedAt());
            }
            postIdList.remove(0);
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