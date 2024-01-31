package com.example.newsfeedservice.core.repository;

import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import com.example.newsfeedservice.core.service.CommentLikeService;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository {

    Optional<CommentLikeEntity> findByUserIdAndCommentId(Long userId, Long commentId);

    CommentLikeEntity save(CommentLikeEntity entity);

    List<CommentLikeActivityDTO> findByIdList(List<Long> idList);
}
