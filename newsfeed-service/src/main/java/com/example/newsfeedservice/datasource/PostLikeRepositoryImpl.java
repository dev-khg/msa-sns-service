package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.PostLikeRepository;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryImpl implements PostLikeRepository {
    private final PostLikeJpaRepository postLikeJpaRepository;

    @Override
    public PostLikeEntity save(PostLikeEntity entity) {
        return postLikeJpaRepository.saveAndFlush(entity);
    }

    @Override
    public Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId) {
        return postLikeJpaRepository.findByUserIdAndPostEntityId(userId, postId);
    }

    @Override
    public List<PostLikeActivityDTO> findByIdList(List<Long> idList) {
        return postLikeJpaRepository.findByIdList(idList);
    }
}
