package com.example.preorder.post.db;

import com.example.preorder.post.core.entity.PostEntity;
import com.example.preorder.post.db.dto.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

    @Query("select p from PostEntity p " +
            "where p.userId in :user_ids")
    List<PostEntity> findPostsByUserId(@Param("user_ids") List<Long> userIds,
                                       Pageable pageable);

//    @Query("select new com.example.preorder.post.db.dto.PostDto(p.id, p.userId, u.username) from PostEntity p " +
//            "left join UserEntity u on u.id = p.userId " +
//            "where p.id = :postId")
//    PostDto findByPostIdFetchUsername(@Param("postId") Long postId);

    List<PostEntity> findByUserIdIn(List<Long> userId, Pageable pageable);
}
