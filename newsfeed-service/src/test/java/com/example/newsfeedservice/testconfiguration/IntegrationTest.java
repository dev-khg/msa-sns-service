package com.example.newsfeedservice.testconfiguration;

import com.example.commonproject.response.ApiResponse;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.CommentLikeRepository;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostLikeRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Disabled
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public abstract class IntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected CommentRepository commentRepository;
    @Autowired
    protected CommentLikeRepository commentLikeRepository;
    @Autowired
    protected PostRepository postRepository;
    @Autowired
    protected PostLikeRepository postLikeRepository;
    @MockBean
    ActivityFeignClient activityFeignClient;
    @PersistenceContext
    protected EntityManager em;

    protected List<PostEntity> postEntities;

    @BeforeEach
    void beforeEach() {
        postEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PostEntity save = postRepository.save(PostEntity.create(1L, createRandomUUID()));
            postEntities.add(save);
        }
        flushAndClearPersistence();
    }

    protected String createRandomUUID() {
        return UUID.randomUUID().toString();
    }

    protected void flushAndClearPersistence() {
        em.flush();
        em.clear();
    }

    protected ApiResponse readResponseJsonBody(String content) throws Exception {
        return objectMapper.readValue(content, ApiResponse.class);
    }

    protected <T> T readResponseJsonBody(String content, Class<T> clazz) throws Exception {
        TypeReference<ApiResponse<T>> typeReference = new TypeReference<>() { };
        ApiResponse<T> apiResponse = objectMapper.readValue(content, typeReference);
        T data = objectMapper.convertValue(apiResponse.getData(), clazz);

        return data;
    }
}
