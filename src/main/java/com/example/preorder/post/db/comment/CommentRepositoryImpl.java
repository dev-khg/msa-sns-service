package com.example.preorder.post.db.comment;

import com.example.preorder.post.core.entity.CommentEntity;
import com.example.preorder.post.core.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryImpl implements CommentRepository {
    @Override
    public CommentEntity save(CommentEntity commentEntity) {
        return null;
    }

    @Override
    public Optional<CommentEntity> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<CommentEntity> findByPostId(Long postId) {
        return null;
    }
}
