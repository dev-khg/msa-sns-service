package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.db.dto.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    PostEntity save(PostEntity postEntity);

    Optional<PostEntity> findById(Long id);

    List<PostEntity> findPostByUserIds(List<Long> userIds, Pageable pageable);

}
