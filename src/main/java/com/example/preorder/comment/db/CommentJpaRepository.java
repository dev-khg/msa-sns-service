package com.example.preorder.comment.db;

import com.example.preorder.comment.core.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

//    @Query("select c from CommentEntity c " +
//            "join fetch c.userEntity " +
//            "join fetch c.postEntity p " +
//            "join fetch p.userEntity " +
//            "where c.id = :id")
//    Optional<CommentEntity> findByIdFJPostAndPostUser(@Param("id") Long id);

    @Query("select c from CommentEntity c " +
            "join fetch c.userEntity " +
            "where c.postEntity.id = :postId")
    List<CommentEntity> findByPostId(@Param("postId") Long postId);

    @Query("select c from CommentEntity c " +
            "join fetch c.postEntity p " +
            "join fetch p.userEntity " +
            "join fetch c.userEntity " +
            "where c.id in (:comment_id_list)")
    List<CommentEntity> findByCommentIdList(@Param("comment_id_list") List<Long> commentIdList);
}
