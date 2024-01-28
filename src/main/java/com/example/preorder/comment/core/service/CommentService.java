package com.example.preorder.comment.core.service;

import com.example.preorder.comment.core.entity.CommentEntity;
import com.example.preorder.comment.core.event.CommentEventPublisher;
import com.example.preorder.comment.core.repository.CommentRepository;
import com.example.preorder.comment.presentation.response.CommentActivityResponse;
import com.example.preorder.comment.presentation.response.CommentInfoResponse;
import com.example.preorder.common.activity.ActivityOffer;
import com.example.preorder.common.activity.ActivityResponse;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.common.exception.InternalErrorException;
import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.user.core.entity.UserEntity;
import com.example.preorder.user.core.repository.UserRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ActivityOffer {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentEventPublisher eventPublisher;

    @Transactional
    public Long enrollComment(Long userId, Long postId, String content) {
        UserEntity userEntity = getUserEntity(userId);
        PostEntity postEntity = getPostEntity(postId);

        CommentEntity commentEntity = CommentEntity.create(userEntity, postEntity, content);

        eventPublisher.handleEnrollComment(userId, commentEntity.getId());

        return commentRepository.save(commentEntity).getId();
    }
    @Override
    @Transactional(readOnly = true)
    public List<ActivityResponse> handleActivity(List<Long> targetIdList) {
        List<CommentEntity> commentByList = commentRepository.findCommentByList(targetIdList);

        return commentByList.stream()
                .map(this::entityToActivityResponse)
                .toList();
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not Found Post.")
        );
    }

    private UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new InternalErrorException("Server Error.")
        );
    }

    private ActivityResponse entityToActivityResponse(CommentEntity commentEntity) {
        return new CommentActivityResponse(commentEntity);
    }
}
