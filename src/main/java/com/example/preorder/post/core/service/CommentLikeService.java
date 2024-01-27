package com.example.preorder.post.core.service;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.event.EventType;
import com.example.preorder.post.core.entity.CommentLikeEntity;
import com.example.preorder.post.core.event.PostEvent;
import com.example.preorder.post.core.repository.CommentLikeRepository;
import com.example.preorder.post.core.vo.CommentLikeStatus;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentLikeService extends EventPublisher {
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    public CommentLikeService(ApplicationEventPublisher publisher, CommentLikeRepository commentLikeRepository, UserRepository userRepository) {
        super(publisher);
        this.commentLikeRepository = commentLikeRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void handleCommentLike(Long userId, String username, Long commentId, CommentLikeStatus status) {
        CommentLikeEntity commentLikeEntity = commentLikeRepository.findByUserIdAndCommentId(
                userId, commentId
        ).orElse(null);

        if (commentLikeEntity == null) {
            commentLikeEntity = CommentLikeEntity.create(userId, commentId, username, status);
        }

        publishEvent(
                new DomainEvent(
                        EventType.POST, PostEvent.COMMENT_LIKE.ordinal(), commentLikeEntity
                )
        );

        commentLikeRepository.save(commentLikeEntity);
    }
}
