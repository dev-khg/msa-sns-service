package com.example.newsfeedservice.core.service;

import com.example.commonproject.event.EventPublisher;
import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import com.example.newsfeedservice.core.service.external.ActivityFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.commonproject.activity.ActivityEvent.create;
import static com.example.commonproject.activity.ActivityType.COMMENT;
import static com.example.commonproject.activity.ActivityType.COMMENT_LIKE;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public Long enrollComment(Long userId, Long postId, String content) {
        PostEntity postEntity = getPostEntity(postId);
        Long savedId = commentRepository.save(CommentEntity.create(userId, postEntity, content)).getId();
        eventPublisher.publish(create(COMMENT, userId, savedId));
        return savedId;
    }

    @Transactional(readOnly = true)
    public List<CommentActivityDTO> getCommentActivities(List<Long> commentIdList) {
        return commentRepository.findCommentByIdList(commentIdList);
    }

    private PostEntity getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new BadRequestException("Not exists post.")
        );
    }
}
