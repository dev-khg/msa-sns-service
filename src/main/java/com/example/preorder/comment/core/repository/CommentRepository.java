package com.example.preorder.comment.core.repository;

import com.example.preorder.comment.core.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    CommentEntity save(CommentEntity commentEntity);

    List<CommentEntity> findByPostId(Long postId);

    List<CommentEntity> findCommentByList(List<Long> commentList);

    Optional<CommentEntity> findById(Long id);
}
