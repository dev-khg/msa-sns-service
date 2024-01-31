package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.PostRepository;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public PostEntity save(PostEntity entity) {
        return postJpaRepository.save(entity);
    }

    @Override
    public List<PostActivityDTO> findPostIdList(List<Long> postIdList) {
        return postJpaRepository.findByIdList(postIdList);
    }

    @Override
    public Optional<PostEntity> findById(Long id) {
        return postJpaRepository.findById(id);
    }
}
