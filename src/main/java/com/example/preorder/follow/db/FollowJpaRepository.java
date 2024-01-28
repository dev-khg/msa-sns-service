package com.example.preorder.follow.db;

import com.example.preorder.follow.core.entity.FollowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowJpaRepository extends JpaRepository<FollowEntity, Long> {

    Optional<FollowEntity> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    List<FollowEntity> findAllByFollowerId(Long followerId);

    @Query("select f from FollowEntity f " +
            "join fetch f.followee " +
            "join fetch f.follower " +
            "where f.followee.id in (:follower_id_list)")
    List<FollowEntity> findAllByFollowerIdIn(@Param("follower_id_list") List<Long> followerIdList);
}
