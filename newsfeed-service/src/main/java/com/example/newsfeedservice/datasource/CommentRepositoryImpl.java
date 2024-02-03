package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.repository.CommentRepository;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public CommentEntity save(CommentEntity commentEntity) {
        return commentJpaRepository.saveAndFlush(commentEntity);
    }

    @Override
    public List<CommentActivityDTO> findCommentByIdList(List<Long> idList) {
        return commentJpaRepository.findByIdList(idList);
    }

    @Override
    public Optional<CommentEntity> findById(Long id) {
        return commentJpaRepository.findById(id);
    }
}
