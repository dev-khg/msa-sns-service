package com.example.newsfeedservice.core.service;

import com.example.commonproject.event.EventPublisher;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commonproject.activity.ActivityEvent.create;
import static com.example.commonproject.activity.ActivityType.COMMENT_LIKE;
import static com.example.commonproject.activity.ActivityType.POST;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public Long enrollPost(Long userId, String content) {
        PostEntity postEntity = PostEntity.create(userId, content);
        Long savedId = postRepository.save(postEntity).getId();

        eventPublisher.publish(create(POST, userId, savedId));

        return savedId;
    }

    @Transactional(readOnly = true)
    public List<PostActivityDTO> getActivities(List<Long> targetIdList) {
        return postRepository.findPostIdList(targetIdList);
    }
}
