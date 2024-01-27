package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.CommentLikeEntity;

import java.util.Optional;

public interface CommentLikeRepository {

    Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);

    CommentLikeEntity save(CommentLikeEntity commentLikeEntity);
}
