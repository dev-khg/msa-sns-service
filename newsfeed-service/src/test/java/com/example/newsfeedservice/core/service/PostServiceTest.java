package com.example.newsfeedservice.core.service;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Disabled;
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
@Disabled
class PostServiceTest {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    @DisplayName("포스트 등록시 디비에서 조회가 가능해야 한다.")
    void enroll_post() {
        // given
        Long userId = 1L;
        String content = createRandomUUID();

        // when
        Long savedId = postService.enrollPost(userId, content);
        flushAndClearPersistence();

        // then
        PostEntity postEntity = postRepository.findById(savedId).orElseThrow();
        assertEquals(postEntity.getUserId(), userId);
        assertEquals(postEntity.getContent(), content);
        assertNotNull(postEntity.getCreatedAt());
        assertNotNull(postEntity.getUpdatedAt());
        assertNull(postEntity.getDeletedAt());
    }

    @Test
    @DisplayName("아이디 리스트로 조회시 올바르게 반환되어야 한다.")
    void find_by_id_list() {
        // given
        Map<Long, PostEntity> postInputMap = new HashMap<>();
        Long userId = 1L;
        for (int i = 0; i < 10; i++) {
            PostEntity postEntity = PostEntity.create(userId, createRandomUUID());
            postRepository.save(postEntity);
            postInputMap.put(postEntity.getId(), postEntity);
        }
        flushAndClearPersistence();

        // when

        // then
        List<Long> postIdList = new ArrayList<>(postInputMap.keySet().stream().toList());

        while (!postIdList.isEmpty()) { // 하나씩 아이디를 지워가면서 조회하고, 결과 유효성 판단
            List<PostActivityDTO> activityDTOS = postService.getActivities(postIdList);
            for (PostActivityDTO activityDTO : activityDTOS) {
                PostEntity postEntity = postInputMap.get(activityDTO.getPostId());

                assertEquals(postEntity.getUserId(), activityDTO.getUserId());
                assertEquals(postEntity.getContent(), activityDTO.getContent());
            }
            postIdList.remove(0);
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