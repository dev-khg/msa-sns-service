package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.PostLikeEntity;
import com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {
    Optional<PostLikeEntity> findByUserIdAndPostEntityId(Long userId, Long postId);

    @Query("select new com.example.newsfeedservice.core.repository.dto.PostLikeActivityDTO" +
            "(pl.userId, pl.postEntity.userId, p.id, pl.updatedAt) " +
            "from PostLikeEntity pl " +
            "left join PostEntity p on p.id = pl.postEntity.id " +
            "where pl.id in (:id_list)")
    List<PostLikeActivityDTO> findByIdList(@Param("id_list") List<Long> idList);
}
