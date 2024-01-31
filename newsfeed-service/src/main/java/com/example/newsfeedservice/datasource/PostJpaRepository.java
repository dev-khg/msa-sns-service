package com.example.newsfeedservice.datasource;

import com.example.newsfeedservice.core.entity.PostEntity;
import com.example.newsfeedservice.core.repository.dto.PostActivityDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    @Query("select new com.example.newsfeedservice.core.repository.dto.PostActivityDTO" +
            "(p.userId, p.id, p.content, p.createdAt) " +
            "from PostEntity p " +
            "where p.id in (:id_list)")
    List<PostActivityDTO> findByIdList(@Param("id_list") List<Long> idList);
}
