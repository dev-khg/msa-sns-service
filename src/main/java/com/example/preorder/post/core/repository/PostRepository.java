package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.PostEntity;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    PostEntity save(PostEntity postEntity);

    Optional<PostEntity> findById(Long postId);

    Optional<PostEntity> findByIdFJUser(Long postId);

    List<PostEntity> findPostIn(List<Long> postIdList);
}
