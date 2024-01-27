package com.example.preorder.comment.core.service;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.comment.core.entity.CommentLikeEntity;
import com.example.preorder.comment.core.entity.CommentLikeStatus;
import com.example.preorder.comment.core.event.CommentLikeEventPublisher;
import com.example.preorder.comment.core.event.list.CommentLikeEvent;
import com.example.preorder.comment.core.repository.CommentLikeRepository;
import com.example.preorder.comment.core.repository.CommentRepository;
import com.example.preorder.comment.presentation.response.CommentLikeActivityResponse;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.preorder.comment.core.entity.CommentLikeStatus.*;
import static com.example.preorder.comment.core.event.list.CommentLikeEvent.*;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentLikeEventPublisher eventPublisher;

    @Transactional
    public void handleCommentLike(Long userId, Long commentId, CommentLikeStatus status) {
        UserEntity userEntity = getUserEntity(userId);
        CommentEntity commentEntity = getCommentEntity(commentId);

        CommentLikeEntity commentLikeEntity = commentLikeRepository.findByUserIdAndCommentId(userId, commentId)
                .orElse(null);

        if (commentLikeEntity == null) {
            commentLikeEntity = CommentLikeEntity.create(commentEntity, userEntity, status);
        }

        commentLikeEntity.changeStatus(status);
        eventPublisher.commentLikeHandle(userId, commentId, status == ACTIVATE ? LIKE : UNLIKE);

        commentLikeRepository.save(commentLikeEntity);
    }

    @Transactional(readOnly = true)
    public List<CommentLikeActivityResponse> getCommentActivity(List<Long> commentLikeIdList) {
        List<CommentLikeEntity> commentLikeEntityList = commentLikeRepository.findByIdList(commentLikeIdList);

        return commentLikeEntityList.stream()
                .map(CommentLikeActivityResponse::new)
                .toList();
    }

    private CommentEntity getCommentEntity(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new BadRequestException("Not found comment")
        );
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InternalErrorException("Server Error.")
        );
    }
}
