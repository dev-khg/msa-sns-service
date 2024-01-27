package com.example.preorder.post.db.comment;

import com.example.preorder.post.core.entity.CommentLikeEntity;
import com.example.preorder.post.core.repository.CommentLikeRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentLikeRepositoryImpl implements CommentLikeRepository {
    @Override
    public Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId) {
        return Optional.empty();
    }

    @Override
    public CommentLikeEntity save(CommentLikeEntity commentLikeEntity) {
        return null;
    }
}
