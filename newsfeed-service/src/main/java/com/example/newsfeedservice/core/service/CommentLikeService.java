package com.example.newsfeedservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.repository.CommentLikeRepository;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void handleCommentLike(Long userId, Long commentId, boolean like) {
        CommentEntity commentEntity = getCommentEntity(commentId);
        CommentLikeEntity commentLikeEntity = getCommentLikeEntity(userId, commentId);

        if (commentLikeEntity != null) {
            commentLikeEntity.makeDelete(!like);
        } else if (like) {
            commentLikeRepository.save(CommentLikeEntity.create(userId, commentEntity));
        }
    }

    @Transactional(readOnly = true)
    public List<CommentLikeActivityDTO> getActivities(List<Long> idList) {
        return commentLikeRepository.findByIdList(idList);
    }

    private CommentLikeEntity getCommentLikeEntity(Long userId, Long commentId) {
        return commentLikeRepository.findByUserIdAndCommentId(userId, commentId).orElse(null);
    }

    private CommentEntity getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException("Not exists comment")
        );
    }
}
