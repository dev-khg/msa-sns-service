package com.example.newsfeedservice.core.service;

import com.example.commonproject.exception.BadRequestException;
import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long enrollComment(Long userId, Long postId, String content) {
        PostEntity postEntity = getPostEntity(postId);
        return commentRepository.save(CommentEntity.create(userId, postEntity, content)).getId();
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
