package com.example.preorder.comment.db;

import com.example.preorder.comment.core.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, Long> {

    @Query("select cl from CommentLikeEntity cl " +
            "join fetch cl.userEntity u " +
            "join fetch cl.commentEntity c " +
            "join fetch c.postEntity p " +
            "join fetch p.userEntity " +
            "where cl.id in (:comment_like_list_id)")
    List<CommentLikeEntity> findByCommentLikeList(@Param("comment_like_list_id") List<Long> commentLikeListId);

    Optional<CommentLikeEntity> findByCommentEntityIdAndUserEntityId(Long commentId, Long userId);

//    @Query("select cl from CommentLikeEntity cl " +
//            "join fetch cl.commentEntity c " +
//            "join fetch c.postEntity p " +
//            "where cl.userEntity.id = :userId and p.id = :postId")
//    Optional<CommentLikeEntity> findUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
