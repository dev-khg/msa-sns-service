package com.example.newsfeedservice.core.repository;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    CommentEntity save(CommentEntity commentEntity);

    List<CommentActivityDTO> findCommentByIdList(List<Long> idList);

    Optional<CommentEntity> findById(Long id);
}
