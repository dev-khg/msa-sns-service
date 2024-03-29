package com.example.newsfeedservice.core.service;

import com.example.commonproject.activity.ActivityEvent;
import com.example.commonproject.activity.ActivityType;
import com.example.commonproject.event.EventPublisher;
import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.repository.CommentLikeRepository;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commonproject.activity.ActivityEvent.*;
import static com.example.commonproject.activity.ActivityType.*;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void handleCommentLike(Long userId, Long commentId, boolean like) {
        CommentEntity commentEntity = getCommentEntity(commentId);
        CommentLikeEntity commentLikeEntity = getCommentLikeEntity(userId, commentId);

        if (commentLikeEntity != null && !like) {
            commentLikeEntity.makeDelete(true);
            eventPublisher.publish(create(COMMENT_UNLIKE, userId, commentLikeEntity.getId()));
        } else if (commentLikeEntity != null && like && commentEntity.getDeletedAt() != null) {
            commentLikeEntity.makeDelete(false);
            eventPublisher.publish(create(COMMENT_LIKE, userId, commentLikeEntity.getId()));
        } else if(commentLikeEntity == null && like) {
            CommentLikeEntity saved = commentLikeRepository.save(CommentLikeEntity.create(userId, commentEntity));
            eventPublisher.publish(create(COMMENT_LIKE, userId, saved.getId()));
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
