package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.CommentEntity;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    CommentEntity save(CommentEntity commentEntity);

    Optional<CommentEntity> findById(Long id);

    List<CommentEntity> findByPostId(Long postId);
}
