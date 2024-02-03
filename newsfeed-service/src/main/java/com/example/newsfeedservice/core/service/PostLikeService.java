package com.example.newsfeedservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.PostLikeRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commonproject.activity.ActivityEvent.create;
import static com.example.commonproject.activity.ActivityType.*;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ActivityFeignClient activityFeignClient;

    @Transactional
    public void handlePostLike(Long userId, Long postId, boolean like) {
        PostEntity postEntity = getPostEntity(postId);
        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(userId, postId).orElse(null);


        if (postLikeEntity != null) {
            postLikeEntity.makeDelete(!like);
            activityFeignClient.handleEvent(create(POST_UNLIKE, userId, postLikeEntity.getId()));
        } else if (like) {
            PostLikeEntity saved = postLikeRepository.save(PostLikeEntity.create(userId, postEntity));
            activityFeignClient.handleEvent(create(POST_LIKE, userId, saved.getId()));
        }
    }

    @Transactional(readOnly = true)
    public List<PostLikeActivityDTO> getActivities(List<Long> idList) {
        return postLikeRepository.findByIdList(idList);
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not exists post.")
        );
    }
}
