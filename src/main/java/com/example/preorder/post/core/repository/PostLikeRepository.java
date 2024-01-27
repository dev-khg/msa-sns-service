package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.vo.PostLikeStatus;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository {
    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    PostLikeEntity save(PostLikeEntity postLikeEntity);

    List<PostLikeEntity> findByUserIds(List<Long> userIds, PostLikeStatus status);
}
