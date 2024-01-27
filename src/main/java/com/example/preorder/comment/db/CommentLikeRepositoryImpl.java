package com.example.preorder.comment.db;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.comment.core.entity.CommentLikeEntity;
import com.example.preorder.comment.core.repository.CommentLikeRepository;
import com.example.preorder.comment.core.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {
    private final CommentLikeJpaRepository commentLikeJpaRepository;

    @Override
    public List<CommentLikeEntity> findByIdList(List<Long> commentLikeIdList) {
        return commentLikeJpaRepository.findByCommentLikeList(commentLikeIdList);
    }

    @Override
    public CommentLikeEntity save(CommentLikeEntity entity) {
        return commentLikeJpaRepository.save(entity);
    }

    @Override
    public Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId) {
        return commentLikeJpaRepository.findByCommentEntityIdAndUserEntityId(commentId, userId);
    }
}
