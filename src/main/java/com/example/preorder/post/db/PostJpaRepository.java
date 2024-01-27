package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    @Query("select p from PostEntity p " +
            "join fetch p.userEntity " +
            "where p.id = :postId and p.deletedAt is null")
    Optional<PostEntity> findByIdFJUser(@Param("postId") Long postId);

    @Query("select p from PostEntity p " +
            "join fetch p.userEntity " +
            "where p.id in (:post_id_list)")
    List<PostEntity> findAllById(@Param("post_id_list") List<Long> postIdList);
}
