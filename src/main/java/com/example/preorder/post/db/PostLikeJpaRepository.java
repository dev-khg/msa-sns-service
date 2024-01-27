package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostLikeEntity;
import com.example.preorder.post.core.vo.PostLikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {
    Optional<PostLikeEntity> findByUserIdAndPostId(Long userId, Long postId);

    List<PostLikeEntity> findAllByUserIdInAndStatusOrderByCreatedAtDesc(List<Long> userId, PostLikeStatus status);

    // public PostLikeResponse(Long userId, Long postId, Long postUserId, String username, String postUsername) {
    @Query("select new com.example.preorder.post.presentation.response.PostLikeResponse(u.id, p.id, p.userEntity.id, u.username, p.userEntity.username) " +
            "from PostEntity p " +
            "left join UserEntity u1 on u1.id = p.userEntity.id " +
            "" +
            "where p.id = :postId")
    void temp(Long whoLikePerson, Long postId);
}
