package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.CommentEntity;
import com.example.newsfeedservice.core.repository.dto.CommentActivityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    @Query("select new com.example.newsfeedservice.core.repository.dto.CommentActivityDTO" +
            "(c.id, c.userId, c.postEntity.id, p.userId, c.createdAt) " +
            "from CommentEntity c " +
            "left join PostEntity p on p.id = c.postEntity.id " +
            "where c.id in (:id_list)")
    List<CommentActivityDTO> findByIdList(@Param("id_list") List<Long> idList);
}
