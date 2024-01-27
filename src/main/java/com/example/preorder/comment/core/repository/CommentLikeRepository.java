package com.example.preorder.comment.core.repository;

import com.example.preorder.comment.core.entity.CommentLikeEntity;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository {

    List<CommentLikeEntity> findByIdList(List<Long> commentLikeIdList);

    CommentLikeEntity save(CommentLikeEntity entity);

    Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);
}
