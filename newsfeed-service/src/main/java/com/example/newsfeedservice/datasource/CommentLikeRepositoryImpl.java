package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.repository.CommentLikeRepository;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentLikeRepositoryImpl implements CommentLikeRepository {
    private final CommentLikeJpaRepository commentLikeJpaRepository;

    @Override
    public Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId) {
        return commentLikeJpaRepository.findByUserIdAndCommentEntityId(userId, commentId);
    }

    @Override
    public CommentLikeEntity save(CommentLikeEntity entity) {
        return commentLikeJpaRepository.save(entity);
    }

    @Override
    public List<CommentLikeActivityDTO> findByIdList(List<Long> idList) {
        return commentLikeJpaRepository.findByIdList(idList);
    }
}
