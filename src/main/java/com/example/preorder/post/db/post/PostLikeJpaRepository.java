package com.example.preorder.post.db.post;

import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.vo.PostLikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {
    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    List<PostLikeEntity> findAllByUserIdInAndStatusOrderByCreatedAtDesc(List<Long> userId, PostLikeStatus status);

}
