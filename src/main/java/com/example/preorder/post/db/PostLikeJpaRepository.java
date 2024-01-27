package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {

    @Query("select pl from PostLikeEntity pl " +
            "where pl.userEntity.id = :userId and pl.postEntity.id = :postId")
    Optional<PostLikeEntity> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("select pl from PostLikeEntity pl " +
            "join fetch pl.userEntity " +
            "join fetch pl.postEntity " +
            "where pl.id in (:post_like_id_list)")
    List<PostLikeEntity> findByIdIn(@Param("post_like_id_list") List<Long> postLikeIdList);
}
