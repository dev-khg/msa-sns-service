package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentLikeEntity;
import com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeJpaRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByUserIdAndCommentEntityId(Long userId, Long commentId);

    @Query("select new com.example.newsfeedservice.core.repository.dto.CommentLikeActivityDTO" +
            "(ci.commentEntity.id, c.userId, ci.userId, ci.updatedAt) " +
            "from CommentLikeEntity ci " +
            "left join CommentEntity c on ci.commentEntity.id = c.id " +
            "where ci.id in (:id_list)")
    List<CommentLikeActivityDTO> findByIdList(@Param("id_list") List<Long> idList);
}
