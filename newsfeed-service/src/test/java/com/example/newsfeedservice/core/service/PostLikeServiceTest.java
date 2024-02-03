package com.example.newsfeedservice.core.service;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.PostLikeRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostLikeServiceTest {
    @Autowired
    PostLikeService postLikeService;
    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    PostRepository postRepository;
    @MockBean
    ActivityFeignClient activityFeignClient;

    @PersistenceContext
    EntityManager em;

    List<PostEntity> savedPost;

    @BeforeEach
    void beforeEach() {
        savedPost = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PostEntity save = postRepository.save(createPostEntity(1L, createRandomUUID()));
            savedPost.add(save);
        }

        flushAndClearPersistence();
    }

    @Test
    @DisplayName("포스트 좋아요 요청시 조회가 가능해야 한다.")
    void save_post_like() {
        // given
        Long userId = 1L;
        PostEntity postEntity = savedPost.get(0);

        // when
        postLikeService.handlePostLike(userId, postEntity.getId(), true);
        flushAndClearPersistence();

        // then
        PostLikeEntity savedPostLike = postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId())
                .orElseThrow();

        assertNotNull(savedPostLike.getId());
        assertNotNull(savedPostLike.getUserId());
        assertEquals(savedPostLike.getPostEntity().getId(), postEntity.getId());
        assertNull(savedPostLike.getDeletedAt());
        assertNotNull(savedPostLike.getCreatedAt());
        assertNotNull(savedPostLike.getUpdatedAt());
    }

    @Test
    @DisplayName("포스트 좋아요 내역이 없으면 좋아요 취소 시 디비에 저장되면 안된다.")
    void save_post_un_like_not_exists_history() {
        // given
        Long userId = 1L;
        PostEntity postEntity = savedPost.get(0);

        // when
        postLikeService.handlePostLike(userId, postEntity.getId(), false);
        flushAndClearPersistence();

        // then
        assertTrue(postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId()).isEmpty());
    }

    @Test
    @DisplayName("포스트 좋아요 내역이 있고, 좋아요 취소 시 정상적으로 반영되어야 한다.")
    void save_post_un_like() {
        // given
        Long userId = 1L;
        PostEntity postEntity = savedPost.get(0);
        postLikeService.handlePostLike(userId, postEntity.getId(), true);
        flushAndClearPersistence();

        // when
        postLikeService.handlePostLike(userId, postEntity.getId(), false);
        flushAndClearPersistence();

        // then
        PostLikeEntity savedEntity
                = postLikeRepository.findByUserIdAndPostId(userId, postEntity.getId()).orElseThrow();

        assertNotNull(savedEntity.getDeletedAt());
    }

    @Test
    @DisplayName("저장된 포스트 좋아요 리스트는 올바르게 조회되어야 한다.")
    void find_post_like_list() {
        // given
        Long userId = 1L;
        PostEntity postEntity = savedPost.get(0);
        Map<Long, PostLikeEntity> postLikeEntityMap = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            PostLikeEntity save = postLikeRepository.save(PostLikeEntity.create(userId + 1, postEntity));
            postLikeEntityMap.put(save.getId(), save);
        }
        flushAndClearPersistence();

        // when

        // then
        List<Long> postLikeIdList = new ArrayList<>(postLikeEntityMap.keySet().stream().toList());
        while (!postLikeIdList.isEmpty()) {
            List<PostLikeActivityDTO> likeActivityDTOS = postLikeService.getActivities(postLikeIdList);
            assertEquals(likeActivityDTOS.size(), postLikeIdList.size());
            postLikeIdList.remove(0);
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