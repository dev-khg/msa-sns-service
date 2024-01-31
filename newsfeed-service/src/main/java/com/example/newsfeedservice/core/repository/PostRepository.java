package com.example.newsfeedservice.core.repository;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    PostEntity save(PostEntity entity);

    List<PostActivityDTO> findPostIdList(List<Long> postIdList);

    Optional<PostEntity> findById(Long id);
}
