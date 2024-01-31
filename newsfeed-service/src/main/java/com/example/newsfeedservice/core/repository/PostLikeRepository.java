package com.example.newsfeedservice.core.repository;

import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository {
    PostLikeEntity save(PostLikeEntity entity);

    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    List<PostLikeActivityDTO> findByIdList(List<Long> idList);
}
