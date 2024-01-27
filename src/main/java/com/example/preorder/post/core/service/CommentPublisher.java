package com.example.preorder.post.core.service;

import com.example.preorder.common.event.DomainEvent;
import com.example.preorder.common.event.EventPublisher;
import com.example.preorder.common.event.EventType;
import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.post.core.entity.CommentEntity;
import com.example.preorder.post.core.event.PostEvent;
import com.example.preorder.post.core.repository.CommentRepository;
import com.example.preorder.post.core.repository.PostRepository;
import com.example.preorder.post.presentation.request.CommentCreateRequest;
import com.example.preorder.post.presentation.response.CommentResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.preorder.post.core.entity.CommentEntity.*;

@Service
public class CommentPublisher extends EventPublisher {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentPublisher(ApplicationEventPublisher publisher, CommentRepository commentRepository, PostRepository postRepository) {
        super(publisher);
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Long enrollComment(Long userId, String username, Long postId, CommentCreateRequest commentCreateRequest) {
        if (postRepository.findById(postId).isEmpty()) {
            throw new BadRequestException("post is not exists");
        }

        CommentEntity commentEntity = create(userId, postId, username, commentCreateRequest.getContent());

        publishEvent(new DomainEvent(
                EventType.POST,
                PostEvent.COMMENT_CREATED.ordinal(),
                commentEntity)
        );

        return commentRepository.save(commentEntity).getId();
    }

    public CommentResponse getComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);

        if (commentEntity == null) {
            throw new RuntimeException("Comment Not found.");
        }

        return CommentResponse.of(commentEntity);
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(CommentResponse::of)
                .toList();
    }
}
