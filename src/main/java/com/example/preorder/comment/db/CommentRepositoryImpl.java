package com.example.preorder.comment.db;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.comment.core.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public CommentEntity save(CommentEntity commentEntity) {
        return commentJpaRepository.save(commentEntity);
    }

    @Override
    public List<CommentEntity> findByPostId(Long postId) {
        return commentJpaRepository.findByPostId(postId);
    }

    @Override
    public List<CommentEntity> findCommentByList(List<Long> commentList) {
        return commentJpaRepository.findByCommentIdList(commentList);
    }

    @Override
    public Optional<CommentEntity> findById(Long id) {
        return commentJpaRepository.findById(id);
    }
}
