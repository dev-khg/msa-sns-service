package com.example.preorder.post.core.repository;

import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.event.list.PostLikeEvent;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository {

    PostLikeEntity save(PostLikeEntity postLikeEntity);

    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    List<PostLikeEntity> findPostLikeByIdList(List<Long> postLikeIdList);
}
