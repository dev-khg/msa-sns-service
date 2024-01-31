package com.example.newsfeedservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.PostLikeRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public void handlePostLike(Long userId, Long postId, boolean like) {
        PostEntity postEntity = getPostEntity(postId);
        PostLikeEntity postLikeEntity = postLikeRepository.findByUserIdAndPostId(userId, postId).orElse(null);


        if (postLikeEntity != null) {
            postLikeEntity.makeDelete(!like);
        } else if (like) {
            postLikeRepository.save(PostLikeEntity.create(userId, postEntity));
        }
    }

    @Transactional(readOnly = true)
    public List<PostLikeActivityDTO> getActivities(List<Long> idList) {
        return postLikeRepository.findByIdList(idList);
    }

    private PostLikeActivityDTO entitiesToActivityDTO(PostLikeEntity postLikeEntity) {
        return new PostLikeActivityDTO(
                postLikeEntity.getUserId(),
                postLikeEntity.getPostEntity().getUserId(),
                postLikeEntity.getPostEntity().getId(),
                postLikeEntity.getUpdatedAt()
        );
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not exists post.")
        );
    }
}
